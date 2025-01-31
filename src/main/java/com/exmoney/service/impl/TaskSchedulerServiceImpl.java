package com.exmoney.service.impl;

import com.exmoney.entity.*;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.NotificationBuilder;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.mapper.ExpenseMapper;
import com.exmoney.payload.mapper.SchedulerMapper;
import com.exmoney.payload.request.scheduler.ExpenseSchedulerRequest;
import com.exmoney.payload.request.expense.ExpenseCreateRequest;
import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.payload.response.scheduler.SchedulerResponse;
import com.exmoney.repository.*;
import com.exmoney.service.CommonService;
import com.exmoney.service.NotificationService;
import com.exmoney.service.TaskSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.exmoney.payload.enumerate.ErrorCode.*;
import static com.exmoney.util.Constant.ExpenseType.SCHEDULE;
import static com.exmoney.util.Constant.NotificationComponent.*;
import static com.exmoney.util.Constant.NotificationPriority.HIGH;
import static com.exmoney.util.Constant.NotificationType.EXPENSE;
import static com.exmoney.util.Constant.ScheduleTimeIntervalType.*;
import static com.exmoney.util.Constant.SchedulerTaskName.TASK_EXPENSE_AUTO;
import static com.exmoney.util.Constant.Status.ACTIVE;
import static com.exmoney.util.Constant.Status.SCHEDULED;
import static com.exmoney.util.Constant.TableName.EXPENSE_TBL;
import static com.exmoney.util.Utils.getNow;

@Service
@RequiredArgsConstructor
public class TaskSchedulerServiceImpl implements TaskSchedulerService {

    private final TaskSchedulerConfigRepository taskSchedulerConfigRepository;
    private final WalletRepository walletRepository;
    private final ExpenseCategoryRepository categoryRepository;
    private final CommonService commonService;
    private final ExpenseMapper expenseMapper;
    private final ResponseFactory responseFactory;
    private final ExpenseRepository expenseRepository;
    private final SchedulerMapper schedulerMapper;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final UserWalletRepository userWalletRepository;

    @Value("${exmoney.application.action_log.task_schedule_expense}")
    private String actionScheduleExpenseConfig;

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<SchedulerResponse>> create(ExpenseSchedulerRequest request, Locale locale) {
        Long currentUserId = commonService.getCurrentUserId();
        LocalDateTime now = getNow();

        timeIntervalTypeChecking(request, locale);

        Optional<Wallet> wallet = walletRepository.findByIdAndOwner(request.getExpense().getWalletId(), currentUserId);
        if (wallet.isEmpty()) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

//        if (taskSchedulerConfigRepository.findDuplicateByOwner(EXPENSE_TBL, TASK_EXPENSE_AUTO, request.getTimeInterval(), request.getTimeValue(), currentUserId).isPresent()) {
//            commonService.throwException(TASK_SCHEDULER_DUPLICATED, locale, null);
//        }

        //chỉ tạo data, không tác động đến amount của ví, không tính vào list như expense khác
        Expense expense = initExpenseData(request.getExpense(), currentUserId, locale);

        TaskSchedulerConfig configured = taskSchedulerConfigRepository.save(
                TaskSchedulerConfig.builder()
                        .refId(expense.getId())
                        .refTable(EXPENSE_TBL)
                        .taskName(TASK_EXPENSE_AUTO)
                        .status(ACTIVE)
                        .timeInterval(request.getTimeInterval())
                        .timeValue(request.getTimeValue())
                        .createdAt(now)
                        .updatedAt(now)
                        .createdBy(currentUserId)
                        .updatedBy(currentUserId)
                        .build()
        );

        ExpenseCategory category = categoryRepository.findById(expense.getCategoryId()).get();

        String title = commonService.getMessageSrc(
                "notify.title.task_expense_scheduler",
                locale
        );
        String content = commonService.getMessageSrcWithParam(
                "notify.content.task_expense_scheduler",
                locale,
                wallet.get().getName(), expense.getAmount(), commonService.getMessageSrc(category.getName(), locale),
                 this.getIntervalMessage(configured.getTimeInterval(), configured.getTimeValue(), locale)
        );

        Set<Long> userIds = userWalletRepository.findUserByWallet(wallet.get().getId())
                .stream().map(User::getId).collect(Collectors.toSet());

        notificationService.pushNotification(
                NotificationBuilder.builder()
                        .userIdList(userIds)
                        .priority(HIGH)
                        .fcmData(Map.of(
                                TITLE, title,
                                CONTENT, content,
                                TYPE, EXPENSE
                        ))
                        .build()
        );

        return responseFactory.success(actionScheduleExpenseConfig, toResponse(configured, currentUserId, locale));
    }

    @Override
    public String getIntervalMessage(String type, int val, Locale locale) {
        return switch (type) {
            case MONTHLY -> commonService.getMessageSrcWithParam("message.expense_scheduler.interval_message.monthly", locale, val);
            case WEEKLY -> commonService.getMessageSrcWithParam("message.expense_scheduler.interval_message.weekly", locale, val);
            case DAILY -> commonService.getMessageSrcWithParam("message.expense_scheduler.interval_message.daily", locale, val);
            case PER_HOUR -> commonService.getMessageSrcWithParam("message.expense_scheduler.interval_message.per_hour", locale, val);
            case PER_MINUTE -> commonService.getMessageSrcWithParam("message.expense_scheduler.interval_message.per_minute", locale, val);
            default -> "";
        };
    }

    private Expense initExpenseData(ExpenseCreateRequest request, Long currentUserId, Locale locale) {
        Optional<Wallet> optWallet = walletRepository.findByIdAndUser(request.getWalletId(), currentUserId);
        if (optWallet.isEmpty()) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        Optional<ExpenseCategory> optCategory = categoryRepository
                .findByIdAndAccess(request.getCategoryId(), request.getWalletId(), currentUserId);
        if (optCategory.isEmpty()) {
            commonService.throwException(CATEGORY_NOT_FOUND, locale, null, request.getCategoryId());
        }

        Expense expense = expenseMapper.toEntity(request);
        expense.setEntryDate(null);
        expense.setType(SCHEDULE);
        expense.setCreatedAt(getNow());
        expense.setCreatedBy(currentUserId);
        expense.setUserId(currentUserId);
        expense.setStatus(SCHEDULED);
        return expenseRepository.save(expense);
    }

    @Override
    public ResponseEntity<BaseResponse<SchedulerResponse>> update(Long id, ExpenseSchedulerRequest request, Locale locale) {
        Long currentUserId = commonService.getCurrentUserId();
        LocalDateTime now = getNow();

        TaskSchedulerConfig oldConfig = taskSchedulerConfigRepository.findByIdAndOwner(id, currentUserId);
        if (oldConfig == null) {
            commonService.throwException(TASK_SCHEDULER_NOT_FOUND, locale, null);
        }

//        if (taskSchedulerConfigRepository.findDuplicateByOwner(EXPENSE_TBL, TASK_EXPENSE_AUTO, request.getTimeInterval(), request.getTimeValue(), currentUserId).isPresent()) {
//            commonService.throwException(TASK_SCHEDULER_DUPLICATED, locale, null);
//        }

        timeIntervalTypeChecking(request, locale);

        oldConfig.setTimeInterval(request.getTimeInterval());
        oldConfig.setTimeValue(request.getTimeValue());
        oldConfig.setUpdatedAt(now);
        oldConfig.setUpdatedBy(currentUserId);
        TaskSchedulerConfig configured = taskSchedulerConfigRepository.save(oldConfig);

        return responseFactory.success(actionScheduleExpenseConfig, toResponse(configured, currentUserId, locale));
    }

    private SchedulerResponse toResponse(TaskSchedulerConfig config, Long userId, Locale locale) {
        SchedulerResponse scheduler = schedulerMapper.toResponse(config);
        scheduler.setTaskName(commonService.getMessageSrc(scheduler.getTaskName(), locale));
        Optional<ExpenseResponse> expResponse = expenseRepository.accessibleById(config.getRefId(), userId, SCHEDULED);
        expResponse.ifPresent(exp -> {
            exp.setCategoryName(commonService.getMessageSrc(exp.getCategoryName(), locale));
            exp.setParentCategoryName(commonService.getMessageSrc(exp.getParentCategoryName(), locale));
            scheduler.setData(exp);
        });
        scheduler.setData(expResponse);
        return scheduler;
    }

    private void timeIntervalTypeChecking(ExpenseSchedulerRequest request, Locale locale) {
        int timeValue = request.getTimeValue();
        switch (request.getTimeInterval()) {
            case MONTHLY -> {
                if (timeValue < 1 || timeValue > 31) { //nhớ check ngày cuối tháng (28,29,30,31) lúc execute task
                    commonService.throwException(TIME_VALUE_NOT_MATCH_INTERVAL, locale, null);
                }
            }
            case WEEKLY -> { //Monday -> Sunday
                if (timeValue < 1 || timeValue > 7) {
                    commonService.throwException(TIME_VALUE_NOT_MATCH_INTERVAL, locale, null);
                }
            }
            case DAILY -> {
                if (timeValue < 1 || timeValue > 24) {
                    commonService.throwException(TIME_VALUE_NOT_MATCH_INTERVAL, locale, null);
                }
            }
            case PER_HOUR, PER_MINUTE -> {
                if (timeValue < 1 || timeValue > 60) {
                    commonService.throwException(TIME_VALUE_NOT_MATCH_INTERVAL, locale, null);
                }
            }
            default -> commonService.throwException(TIME_INTERVAL_TYPE_NOT_FOUND, locale, null);
        }
    }
}
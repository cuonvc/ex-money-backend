package com.exmoney.worker;

import com.exmoney.entity.Expense;
import com.exmoney.entity.ExpenseCategory;
import com.exmoney.entity.TaskSchedulerConfig;
import com.exmoney.payload.common.NotificationBuilder;
import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.repository.ExpenseCategoryRepository;
import com.exmoney.repository.ExpenseRepository;
import com.exmoney.repository.TaskSchedulerConfigRepository;
import com.exmoney.service.CommonService;
import com.exmoney.service.ExpenseService;
import com.exmoney.service.NotificationService;
import com.exmoney.service.TaskSchedulerService;
import com.exmoney.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.exmoney.util.Constant.ExpenseEntryType.EXPENSE;
import static com.exmoney.util.Constant.NotificationComponent.*;
import static com.exmoney.util.Constant.NotificationPriority.HIGH;
import static com.exmoney.util.Constant.ScheduleTimeIntervalType.*;
import static com.exmoney.util.Constant.SchedulerTaskName.TASK_EXPENSE_AUTO;
import static com.exmoney.util.Constant.Status.ACTIVE;
import static com.exmoney.util.Constant.Status.EXECUTED;
import static com.exmoney.util.Constant.TableName.EXPENSE_TBL;
import static com.exmoney.util.Utils.getNow;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpenseSchedulerExecuteWorker {

    private final TaskSchedulerConfigRepository taskSchedulerConfigRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseService expenseService;
    private final NotificationService notificationService;
    private final CommonService commonService;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final TaskSchedulerService taskSchedulerService;

    @Scheduled(cron = "0 * * * * *")
    public void run() {
        log.info("\n===============> Starting scan Expense scheduler");

        List<TaskSchedulerConfig> tasks = taskSchedulerConfigRepository.findAllByExpenseScheduling(EXPENSE_TBL, TASK_EXPENSE_AUTO);
        tasks.forEach(task -> {
            String interval = task.getTimeInterval();
            int timePoint = task.getTimeValue();
            LocalDateTime now = getNow();

            if (interval.equals(PER_MINUTE)) { //giây thứ bao nhiêu mỗi phút
                log.info("\n===============> X seconds PER_MINUTE DO NOT SUPPORT");
            } else if (interval.equals(PER_HOUR)) {
                log.info("\n===============> X minutes PER_HOUR DO NOT SUPPORT");
            } else if (interval.equals(DAILY)) { //vào mấy giờ hằng ngày

                if (now.getHour() == timePoint) {
                    log.info("\n-----> Trigger DAILY task executing...");
                    updateConfig(task, now);
                    expenseService.executeFromScheduler(task, Locale.getDefault(), now);
                    pushNotification(task, now);
                }
            } else if (interval.equals(WEEKLY)) {
                if (now.getDayOfWeek().getValue() == timePoint) {
                    log.info("\n-----> Trigger WEEKLY task executing...");
                    updateConfig(task, now);
                    expenseService.executeFromScheduler(task, Locale.getDefault(), now);
                    pushNotification(task, now);
                }
            } else if (interval.equals(MONTHLY)) {
                if (now.getDayOfMonth() == timePoint) {
                    log.info("\n-----> Trigger MONTHLY task executing...");
                    updateConfig(task, now);
                    expenseService.executeFromScheduler(task, Locale.getDefault(), now);
                    pushNotification(task, now);
                }
            }
        });

        taskSchedulerConfigRepository.saveAll(tasks);

        log.info("\n===============> End scan Expense scheduler");
    }

    private void updateConfig(TaskSchedulerConfig config, LocalDateTime now) {
        config.setStatus(EXECUTED);
        config.setUpdatedAt(now);
        config.setUpdatedBy(0L);
    }

    private void pushNotification(TaskSchedulerConfig config, LocalDateTime now) {
        Expense expense = expenseRepository.findById(config.getRefId()).orElse(null);
        if (expense != null) {
            ExpenseCategory category = expenseCategoryRepository.findById(expense.getCategoryId()).orElse(null);
            String title = commonService.getMessageSrc(
                    "notify.title.task_expense_scheduler.execute",
                    Locale.getDefault()
            );
            String content = commonService.getMessageSrcWithParam(
                    "notify.content.task_expense_scheduler.execute",
                    Locale.getDefault(),
                    expense.getAmount(),
                    category != null ? category.getName() : "",
                    taskSchedulerService.getIntervalMessage(config.getTimeInterval(), config.getTimeValue(), Locale.getDefault())
            );

            notificationService.pushNotification(
                    NotificationBuilder.builder()
                            .userIdList(Set.of(expense.getCreatedBy()))
                            .priority(HIGH)
                            .fcmData(Map.of(
                                    TITLE, title,
                                    CONTENT, content,
                                    TYPE, Constant.NotificationType.EXPENSE
                            ))
                            .build()
            );
        }
    }
}

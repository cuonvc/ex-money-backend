package com.exmoney.service.impl;

import com.exmoney.entity.*;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.NotificationBuilder;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.mapper.SchedulerMapper;
import com.exmoney.payload.mapper.UserMapper;
import com.exmoney.payload.mapper.WalletMapper;
import com.exmoney.payload.request.wallet.WalletRequest;
import com.exmoney.payload.request.wallet.WalletSettingRequest;
import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.payload.response.scheduler.SchedulerResponse;
import com.exmoney.payload.response.user.UserResponse;
import com.exmoney.payload.response.wallet.WalletResponse;
import com.exmoney.repository.*;
import com.exmoney.security.CustomUserDetail;
import com.exmoney.service.CommonService;
import com.exmoney.service.NotificationService;
import com.exmoney.service.TaskSchedulerService;
import com.exmoney.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.exmoney.payload.enumerate.ErrorCode.*;
import static com.exmoney.util.Constant.DEFAULT_PAGE_OFFSET;
import static com.exmoney.util.Constant.DEFAULT_PAGE_SIZE;
import static com.exmoney.util.Constant.NotificationComponent.*;
import static com.exmoney.util.Constant.NotificationPriority.HIGH;
import static com.exmoney.util.Constant.NotificationPriority.NORMAL;
import static com.exmoney.util.Constant.NotificationType.WALLET;
import static com.exmoney.util.Constant.Status.*;
import static com.exmoney.util.Constant.TableName.EXPENSE_TBL;
import static com.exmoney.util.Constant.WalletChangeUserAction.ADD;
import static com.exmoney.util.Constant.WalletChangeUserAction.REMOVE;
import static com.exmoney.util.Utils.*;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserWalletRepository userWalletRepository;
    private final CommonService commonService;
    private final NotificationService notificationService;
    private final WalletMapper walletMapper;
    private final ExpenseRepository expenseRepository;
    private final ResponseFactory responseFactory;
    private final UserMapper userMapper;
    private final WalletHistoryRepository walletHistoryRepository;
    private final TaskSchedulerService taskSchedulerService;
    private final TaskSchedulerConfigRepository taskSchedulerConfigRepository;
    private final SchedulerMapper schedulerMapper;

    @Value("${exmoney.application.action_log.wallet_create}") //chu y
    private String actionWalletCreate;
    @Value("${exmoney.application.action_log.wallet_add_user}")
    private String actionWalletAddUser;
    @Value("${exmoney.application.action_log.wallet_remove_user}")
    private String actionWalletRemoveUser;
    @Value("action.wallet_change_expense_limit")
    private String actionWalletChangeExpenseLimit;

    @Value("${exmoney.application.default.wallet_name}")
    private String defaultWalletName;
    @Value("${exmoney.application.default.wallet_description}")
    private String defaultWalletDescription;

    @Override
    @Transactional
    public void initDefaultWallet(Long userId, Locale locale) {
//        if (!walletRepository.findByUserId(userId).isEmpty()) {
//            commonService.throwException(INTERNAL_SERVER_ERROR, locale, null);
//        }
        LocalDateTime now = LocalDateTime.now();

        //login qua email thì chắc chắn là mới rồi, còn login qua OAuth thì sẽ luôn đi qua đây nên phải check
        Wallet wallet = walletRepository.findDefaultByOwner(userId)
                        .orElse(new Wallet());

        if (wallet.getId() == null) { //lần đầu
            wallet.setCreatedAt(now);
            wallet.setCreatedBy(0L);
            wallet.setIsDefault(true);
            wallet.setName(defaultWalletName); //lưu là default.wallet_name luôn để có thể get dynamic
            wallet.setDescription(defaultWalletDescription);
            wallet.setOwnerUserId(userId);
            walletRepository.save(wallet);

            userWalletRepository.save(
                    UserWallet.builder()
                            .userId(userId)
                            .walletId(wallet.getId())
                            .updatedAt(now)
                            .status(ACTIVE)
                            .build());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<Wallet>> create(WalletRequest request, Locale locale) {
        Long userId = commonService.getCurrentUserId();
        if (walletRepository.findByNameOfUser(request.getName(), userId).isPresent()) {
            commonService.throwException(WALLET_NAME_ALREADY_EXISTED, locale, null, request.getName());
        }
        Wallet wallet = walletMapper.toEntity(request);
        wallet.setCreatedAt(getNow());
        wallet.setCreatedBy(userId);
        wallet.setOwnerUserId(userId);
        wallet.setIsDefault(false);
        wallet = walletRepository.save(wallet);

        userWalletRepository.save(
                UserWallet.builder()
                        .userId(userId)
                        .walletId(wallet.getId())
                        .updatedAt(getNow())
                        .status(ACTIVE)
                        .build()
        );
        return responseFactory.success(actionWalletCreate, wallet);
    }

    @Override
    public ResponseEntity<BaseResponse<WalletResponse>> detail(Long walletId, Locale locale) {
        Long currentUserId = commonService.getCurrentUserId();
        List<Wallet> wallets = walletRepository.findByUserId(currentUserId, false);
        if (walletId != null) {
            walletId = wallets.stream().filter(Wallet::getIsDefault).findFirst().get().getId();
        }
        final Long finalWalletId = walletId;
        Optional<Wallet> otpWallet = wallets.stream().filter(w -> w.getId().equals(finalWalletId)).findFirst();
        if (otpWallet.isEmpty()) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        Wallet walletObj = otpWallet.get();
        if (walletObj.getIsDefault()) {
            walletObj.setName(commonService.getMessageSrc(walletObj.getName(), locale));
            walletObj.setDescription(commonService.getMessageSrc(walletObj.getDescription(), locale));
        }
        WalletResponse response = walletMapper.toResponse(walletObj);
        List<Map<Long, String>> walletMap = wallets.stream()
                .map(w -> Map.of(w.getId(), w.getName()))
                .toList();
        response.setOtherWallets(walletMap);

        List<ExpenseResponse> expenseResponses = expenseRepository.findAccessByUser(currentUserId, walletId, null, null, null, null, null, DEFAULT_PAGE_OFFSET, DEFAULT_PAGE_SIZE)
                .stream().peek(e -> {
                    e.setWalletName(response.getName());
                    e.setDescription(commonService.getMessageSrc(e.getDescription(), locale));
                    e.setCategoryName(commonService.getMessageSrc(e.getCategoryName(), locale));
                    e.setType(commonService.getMessageSrc(getExpenseTypeDisp(e.getType()), locale));
                })
                .toList();
        response.setExpenses(expenseResponses);

        return responseFactory.success(null, response);
    }

    @Override
    public ResponseEntity<BaseResponse<List<WalletResponse>>> listByUser(boolean isOwner, Locale locale) {
        Long userId = commonService.getCurrentUserId();
        List<WalletResponse> responseList = walletRepository.findByUserId(userId, isOwner)
                .stream().map(w -> toWalletResponse(w, userId, locale)).toList();

        return responseFactory.success(null, responseList);
    }

    private WalletResponse toWalletResponse(Wallet wallet, Long userId, Locale locale) {
        if (wallet.getIsDefault()) {
            wallet.setName(commonService.getMessageSrc(wallet.getName(), locale));
            wallet.setDescription(commonService.getMessageSrc(wallet.getDescription(), locale));
        }
        WalletResponse response = walletMapper.toResponse(wallet);
        List<UserResponse> memberList = userWalletRepository.findUserByWallet(wallet.getId())
                .stream().map(userMapper::entityToResponse)
                .toList();
        List<ExpenseResponse> expenseResponses = expenseRepository.findAccessByUser(userId, wallet.getId(), null, null, null, null, null, DEFAULT_PAGE_OFFSET, DEFAULT_PAGE_SIZE)
                .stream().peek(e -> {
                    e.setWalletName(commonService.getMessageSrc(wallet.getName(), locale));
                    e.setDescription(commonService.getMessageSrc(e.getDescription(), locale));
                    e.setCategoryName(commonService.getMessageSrc(e.getCategoryName(), locale));
                    e.setParentCategoryName(commonService.getMessageSrc(e.getParentCategoryName(), locale));
                    e.setType(commonService.getMessageSrc(getExpenseTypeDisp(e.getType()), locale));
                })
                .toList();
        //hơi chậm tí mà thôi kệ
        List<SchedulerResponse> schedulerResponses = taskSchedulerConfigRepository.findAllByOwnerAndExpenseRef(userId, wallet.getId())
                .stream().map(s -> {
                    SchedulerResponse scheduler = schedulerMapper.toResponse(s);
                    scheduler.setTaskName(commonService.getMessageSrc(scheduler.getTaskName(), locale));
                    Optional<ExpenseResponse> expense = expenseRepository.accessibleById(s.getRefId(), userId, SCHEDULED);
                    expense.ifPresent(exp -> {
                        exp.setCategoryName(commonService.getMessageSrc(exp.getCategoryName(), locale));
                        exp.setParentCategoryName(commonService.getMessageSrc(exp.getParentCategoryName(), locale));
                        exp.setType(commonService.getMessageSrc(getExpenseTypeDisp(exp.getType()), locale));
                        scheduler.setData(exp);
                    });
                    return scheduler;
                }).collect(Collectors.toList());
        response.setMembers(memberList);
        response.setExpenses(expenseResponses);
        response.setSchedulers(schedulerResponses);
        return response;
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<WalletResponse>> changeUser(String action, Long walletId, String userEmail, Locale locale) {
        CustomUserDetail userDetail = commonService.getCurrentUser();
        User targetUser = commonService.findUserByEmailOrThrow(userEmail, locale, null);
        if (targetUser.getId().equals(userDetail.getId())) {
            commonService.throwException(INTERNAL_SERVER_ERROR, locale, null);
        }
        if (walletRepository.findByIdAndOwner(walletId, userDetail.getId()).isEmpty()) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        Optional<Wallet> wallet = walletRepository.findByIdAndUser(walletId, targetUser.getId());
        UserWallet userWallet = new UserWallet();
        String actionLog = "";
        String notiTitle = "";
        String notiContent = "";
        Wallet toResponse = walletRepository.findById(walletId).get();
        if (action.equals(ADD)) {
            if (wallet.isPresent()) {
                commonService.throwException(WALLET_IN_USE_BY_USER, locale, null);
            }
            //must empty
            userWallet = UserWallet.builder()
                    .userId(targetUser.getId())
                    .walletId(walletId)
                    .updatedAt(getNow())
                    .status(ACTIVE)
                    .build();
            actionLog = actionWalletAddUser;
            notiTitle = commonService.getMessageSrcWithParam("notify.title.wallet_add_user", locale);
            notiContent = commonService.getMessageSrcWithParam("notify.content.wallet_add_user", locale, userDetail.getUsername(), toResponse.getName());
        } else if (action.equals(REMOVE)) {
            if (wallet.isEmpty()) {
                commonService.throwException(WALLET_NOT_CONTAINS_USER, locale, null);
            }
            //must exist
            userWallet = userWalletRepository.findByUserAndWallet(targetUser.getId(), walletId);
            userWallet.setStatus(DELETED);
            actionLog = actionWalletRemoveUser;
            notiTitle = commonService.getMessageSrcWithParam("notify.title.wallet_remove_user", locale);
            notiContent = commonService.getMessageSrcWithParam("notify.content.wallet_remove_user", locale, userDetail.getUsername(), toResponse.getName());
        } else {
            commonService.throwException(INTERNAL_SERVER_ERROR, locale, null);
        }


        userWalletRepository.save(userWallet);

        WalletResponse response = toResponse != null
                ? toWalletResponse(toResponse, userDetail.getId(), locale)
                : new WalletResponse();

        notificationService.pushNotification(NotificationBuilder.builder()
                .userIdList(Set.of(targetUser.getId()))
                .priority(HIGH)
                .fcmData(Map.of(
                        TITLE, notiTitle,
                        CONTENT, notiContent,
                        TYPE, WALLET))
                .build());

        return responseFactory.success(actionLog, response);
    }

    @Override
    //hàm này dùng cho việc hiển thị tên ví mặc định của user này với user khác khi join chung ví (tránh nhầm lẫn)
    public String getDisplayWalletName(Wallet wallet, Long currentUserId, String ownerWallet, Locale locale) {
        if (wallet.getOwnerUserId().equals(currentUserId)) {
            return commonService.getMessageSrc(wallet.getName(), locale); //nếu chính chủ, hiện như bình thường
        } else {
            if (wallet.getName().equals("default.wallet_name")) {
                if (ownerWallet == null) {
                    User owner = commonService.findUserByIdOrThrow(wallet.getOwnerUserId(), locale, null);
                    ownerWallet = owner.getName();
                }
                return commonService.getMessageSrcWithParam("default.wallet_name_display_other_user", locale, ownerWallet);
            } else {
                return wallet.getName();
            }
        }
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<WalletResponse>> setting(Long walletId, WalletSettingRequest request, Locale locale) {
        Long userId = commonService.getCurrentUser().getId();
        LocalDateTime now = getNow();
        Optional<Wallet> wallet = walletRepository.findByIdAndOwner(walletId, userId);
        if (wallet.isEmpty()) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        Wallet walletObj = wallet.get();
        //lưu lại lịch sử cái đã
        WalletHistory history = walletMapper.entityToHistory(walletObj);
        history.setUpdatedAt(now);
        history.setCreatedAt(now);
        history.setUpdatedBy(userId);
        history.setCreatedBy(userId);

        if (request.getTotalExpenseLimit() != null) {
            walletObj.setExpenseLimit(getMaxWithZero(request.getTotalExpenseLimit()));
        }
        if (request.getExpenseWarningLevel1() != null) {
            walletObj.setExpenseWarningLevel1(getMaxWithZero(request.getExpenseWarningLevel1()));
        }
        if (request.getExpenseWarningLevel2() != null) {
            walletObj.setExpenseWarningLevel2(getMaxWithZero(request.getExpenseWarningLevel2()));
        }
        if (request.getExpenseWarningLevel3() != null) {
            walletObj.setExpenseWarningLevel3(getMaxWithZero(request.getExpenseWarningLevel3()));
        }

        walletRepository.save(walletObj);
        walletHistoryRepository.save(history);

        String notiTitle = commonService.getMessageSrcWithParam(
                "notify.title.wallet_change_limit", locale);
        String notiContent = commonService.getMessageSrcWithParam(
                "notify.content.wallet_change_limit",
                locale, commonService.getMessageSrc(walletObj.getName(), locale), history.getExpenseLimit(), walletObj.getExpenseLimit());

        notificationService.pushNotification(
                NotificationBuilder.builder()
                        .userIdList(userWalletRepository.findUserByWallet(walletId).stream()
                                .map(User::getId).collect(Collectors.toSet()))
                        .priority(NORMAL)
                        .fcmData(Map.of(
                                TITLE, notiTitle,
                                CONTENT, notiContent,
                                TYPE, WALLET)
                        )
                        .build()
        );

        return responseFactory.success(actionWalletChangeExpenseLimit,
                toWalletResponse(walletObj, userId, locale)
        );
    }
}

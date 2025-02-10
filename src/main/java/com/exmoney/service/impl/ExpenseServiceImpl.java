package com.exmoney.service.impl;

import com.exmoney.entity.*;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.NotificationBuilder;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.mapper.ExpenseMapper;
import com.exmoney.payload.request.expense.ExpenseCreateRequest;
import com.exmoney.payload.request.expense.ExpenseUpdateRequest;
import com.exmoney.payload.response.expense.ExpenseConfirmResponse;
import com.exmoney.payload.response.expense.ExpenseEditResource;
import com.exmoney.payload.response.expense.ExpenseFilterResource;
import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.payload.response.expenseCategory.ExpenseCategoryResponse;
import com.exmoney.repository.*;
import com.exmoney.security.CustomUserDetail;
import com.exmoney.service.*;
import com.exmoney.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.exmoney.payload.enumerate.ErrorCode.*;
import static com.exmoney.util.Constant.ExpenseEntryType.*;
import static com.exmoney.util.Constant.ExpenseType.*;
import static com.exmoney.util.Constant.NotificationComponent.*;
import static com.exmoney.util.Constant.NotificationPriority.CRITICAL;
import static com.exmoney.util.Constant.NotificationPriority.HIGH;
import static com.exmoney.util.Constant.NotificationType.WALLET;
import static com.exmoney.util.Constant.Status.*;
import static com.exmoney.util.Utils.clientToLocalDateTime;
import static com.exmoney.util.Utils.getNow;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseMapper expenseMapper;
    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository categoryRepository;
    private final WalletRepository walletRepository;
    private final CommonService commonService;
    private final ResponseFactory responseFactory;
    private final ExpenseCategoryService categoryService;
    private final UserWalletRepository userWalletRepository;
    private final WalletService walletService;
    private final UserRepository userRepository;
    private final ExpenseHistoryRepository expenseHistoryRepository;
    private final NotificationService notificationService;

    @Value("${exmoney.application.default.expense_income_name}")
    private String expenseIncomeName;

    @Value("${exmoney.application.default.expense_income_description}")
    private String expenseIncomeDescription;

    @Value("${exmoney.application.action_log.expense_create}")
    private String actionLogExpenseCreate;

    @Value("${exmoney.application.action_log.expense_update}")
    private String actionLogExpenseUpdate;

    @Value("${exmoney.application.action_log.expense_speech_to_text}")
    private String actionLogExpenseSpeechToText;

    @Value("${exmoney.application.action_log.expense_delete}")
    private String actionLogExpenseDelete;

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<ExpenseResponse>> create(ExpenseCreateRequest request, Locale locale) {

        CustomUserDetail userDetail = commonService.getCurrentUser();
        Long currentUserId = userDetail.getId();
        Optional<Wallet> optWallet = walletRepository.findByIdAndUser(request.getWalletId(), currentUserId);
        if (optWallet.isEmpty()) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        LocalDateTime entryDate;
        if (request.getEntryDate() == null || request.getEntryDate().isEmpty()) {
            entryDate = getNow();
        } else {
            entryDate = clientToLocalDateTime(request.getEntryDate());
        }

        Optional<ExpenseCategory> optCategory = categoryRepository
                .findByIdAndAccess(request.getCategoryId(), request.getWalletId(), currentUserId);
        if (optCategory.isEmpty()) {
            commonService.throwException(CATEGORY_NOT_FOUND, locale, null, request.getCategoryId());
        }

        Expense expense = expenseMapper.toEntity(request);
        Wallet wallet = optWallet.get();
        expense.setEntryDate(entryDate);
        expense.setCreatedAt(getNow());
        expense.setCreatedBy(currentUserId);
        expense.setUserId(currentUserId);
        amountDivision(expense, wallet);
        if (!ENTRY_TYPES.contains(request.getEntryType())) {
            commonService.throwException(INTERNAL_SERVER_ERROR, locale, null);
        }
        if (expense.getEntryType().equals(INCOME)) { //nếu là income, không set name, description
            expense.setDescription(expenseIncomeDescription);
            expense.setCategoryId(null);
        } else {
            warningChecker(wallet, locale, currentUserId);
        }
        if (!EXPENSE_TYPES.contains(request.getType())) {
            commonService.throwException(INTERNAL_SERVER_ERROR, locale, null);
        }
        expense.setStatus(request.getType().equals(MANUAL) ? ACTIVE : PENDING);

        expense = expenseRepository.save(expense);
        wallet = walletRepository.save(wallet);

        return doResponse(expense, wallet, optCategory.get(), userDetail, locale, true);
    }

    private void warningChecker(Wallet wallet, Locale locale, Long currentUserId) {
        if (wallet.getExpenseLimit() != null) {

            BigDecimal percentReached = getPercentReached(wallet);
            if (percentReached.compareTo(BigDecimal.ZERO) > 0) {

                String title = commonService.getMessageSrc(
                        "notify.title.wallet_reached_expense_limit",
                        locale);
                String content = commonService.getMessageSrcWithParam(
                        "notify.content.wallet_reached_expense_limit",
                        locale, wallet.getName(), percentReached);

                notificationService.pushNotification(
                        NotificationBuilder.builder()
                                .userIdList(Set.of(currentUserId))
                                .priority(CRITICAL)
                                .fcmData(Map.of(
                                        TITLE, title,
                                        CONTENT, content,
                                        TYPE, EXPENSE)
                                )
                                .build()
                );
            }
        }
    }

    private static BigDecimal getPercentReached(Wallet wallet) {
        BigDecimal percentReached = BigDecimal.ZERO;

        if (wallet.getExpenseLimit().compareTo(BigDecimal.ZERO) == 0) {
            return percentReached;
        }

        BigDecimal newPercent = wallet.getTotalExpense()
                .divide(wallet.getExpenseLimit(), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        if (newPercent.compareTo(BigDecimal.valueOf(100)) >= 0) {
            percentReached = BigDecimal.valueOf(100);
        } else if (newPercent.compareTo(wallet.getExpenseWarningLevel3()) >= 0) {
            percentReached = wallet.getExpenseWarningLevel3();
        } else if (newPercent.compareTo(wallet.getExpenseWarningLevel2()) >= 0) {
            percentReached = wallet.getExpenseWarningLevel2();
        } else if (newPercent.compareTo(wallet.getExpenseWarningLevel1()) >= 0) {
            percentReached = wallet.getExpenseWarningLevel1();
        }
        return percentReached;
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<ExpenseResponse>> update(Long id, ExpenseUpdateRequest request, Locale locale) {
        CustomUserDetail userDetail = commonService.getCurrentUser();
        Long currentUserId = userDetail.getId();
        Expense expense = expenseRepository.findByIdAndOwner(id, currentUserId);
        if (expense == null) {
            commonService.throwException(EXPENSE_NOT_FOUND_OR_NOT_ACCESSIBLE, locale, null);
        }

        //clone sang history đã rồi làm gì thì làm
        ExpenseHistory history = expenseMapper.toHistory(expense);
        expenseHistoryRepository.save(history);

        Wallet wallet = walletRepository.findById(expense.getWalletId()).orElse(null);
        if (wallet == null) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        Optional<ExpenseCategory> optCategory = categoryRepository
                .findByIdAndAccess(request.getCategoryId(), wallet.getId(), currentUserId);
        if (optCategory.isEmpty()) {
            commonService.throwException(CATEGORY_NOT_FOUND, locale, null, request.getCategoryId());
        }

        resetOldAmountInWallet(expense, wallet); //xóa số tiền cũ của expense
        if (expense.getEntryType().equals(EXPENSE)) {
            expense.setDescription(request.getDescription());
            expense.setCategoryId(request.getCategoryId());
        }
        expense.setAmount(request.getAmount());
        expense.setUpdatedAt(getNow());
        if (request.getEntryDate() != null && !request.getEntryDate().isEmpty()) {
            LocalDateTime entryDate = clientToLocalDateTime(request.getEntryDate());
            expense.setEntryDate(entryDate);
        }

        if (expense.getStatus().equals(ACTIVE) && !expense.getType().equals(SCHEDULE)) {
            amountDivision(expense, wallet); //update lại số tiền
            warningChecker(wallet, locale, currentUserId);
        }
        expense = expenseRepository.save(expense);
        wallet = walletRepository.save(wallet);

        return doResponse(expense, wallet, optCategory.get(), userDetail, locale, false);
    }

    @Override
    public ResponseEntity<BaseResponse<ExpenseConfirmResponse>> suggestFromSpeech(String textFromSpeech, Locale locale) {
        //format ex: "50.000" "đổ xăng" ví "cá nhân" - AI chạy bằng cơm
        // không nhận diện được tiền -> throw
        // không nhận diện được category -> lấy other category
        // không nhận diện đụược ví -> lấy ví mặc định

        Long currentUserId = commonService.getCurrentUserId();
        BigDecimal amount = null;
        Wallet wallet = null;
        ExpenseCategory category = null;

        //get amount
        amount = getAmountFromSpeech(textFromSpeech);
        //get wallet
        wallet = getWalletFromSpeech(textFromSpeech, currentUserId, locale);
        //get category
        if (wallet != null) {
            category = getCategoryFromSpeech(textFromSpeech, currentUserId, wallet.getId(), locale);
        }

        ExpenseConfirmResponse response = ExpenseConfirmResponse.builder()
                .description(textFromSpeech)
                .amount(amount)
                .build();
        if (wallet != null) {
            response.setWalletId(wallet.getId());
            response.setWalletName(commonService.getMessageSrc(wallet.getName(), locale));
        }
        if (category != null) {
            response.setCategoryId(category.getId());
            response.setCategoryName(commonService.getMessageSrc(category.getName(), locale));
        }

        return responseFactory.success(actionLogExpenseSpeechToText, response); //tạm
    }

    private BigDecimal getAmountFromSpeech(String text) {
        Pattern pattern = Pattern.compile("([\\d.,]+)\\s*(triệu|đ|nghìn|ngàn|k|ca)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        BigDecimal amount = null;

        while (matcher.find()) {
            String numberStr = matcher.group(1);
            String unit = matcher.group(2);

            if ("triệu".equalsIgnoreCase(unit)) {
                // Handle decimal separator for "triệu"
                numberStr = numberStr.replace(",", ".");
                double value = Double.parseDouble(numberStr);
                amount = BigDecimal.valueOf(Math.round(value * 1_000_000));
            } else if ("nghìn".equalsIgnoreCase(unit) || "ngàn".equalsIgnoreCase(unit)
                    || "ca".equalsIgnoreCase(unit) || "k".equalsIgnoreCase(unit)) {
                // Handle decimal separator for "ngìn"
                numberStr = numberStr.replace(",", ".");
                double value = Double.parseDouble(numberStr);
                amount = BigDecimal.valueOf(Math.round(value * 1_000));
            } else {
                // Handle thousand separators for plain numbers
                numberStr = numberStr.replace(".", "").replace(",", "");
                amount = BigDecimal.valueOf(Integer.parseInt(numberStr));
            }
        }

        return amount;
    }

    private Wallet getWalletFromSpeech(String textFromSpeech, Long userId, Locale locale) {
        List<Wallet> list = walletRepository.findByUserId(userId, false);
        Wallet result = list.stream()
                .filter(wallet -> textFromSpeech.toUpperCase().contains(commonService.getMessageSrc(wallet.getName(), locale).toUpperCase()))
                .findFirst().orElse(null);
        if (result == null) {
            result = list.stream().filter(Wallet::getIsDefault).findFirst().orElse(null);
        }
        return result;
    }

    private ExpenseCategory getCategoryFromSpeech(String textFromSpeech, Long userId, Long walletId, Locale locale) {
        List<ExpenseCategory> all = categoryRepository.findAllByUserAndWallet(walletId, userId);

        ExpenseCategory matchedCategory = all.stream()
                .filter(category -> textFromSpeech.toUpperCase().contains(commonService.getMessageSrc(category.getName(), locale).toUpperCase()))
                .findFirst().orElse(null);

        if (matchedCategory == null) {
            matchedCategory = all.stream()
                    .filter(category -> category.getName().equals("default.category.other"))
                    .findFirst().orElse(null);
        }

        return matchedCategory;
    }

    private ResponseEntity<BaseResponse<ExpenseResponse>> doResponse(Expense expense, Wallet wallet,
                                                                     ExpenseCategory category, CustomUserDetail userDetail,
                                                                     Locale locale, boolean doCreate) {
        ExpenseResponse response = expenseMapper.toResponse(expense);
        response.setWalletName(commonService.getMessageSrc(wallet.getName(), locale));
        response.setUserName(userDetail.getName());
        response.setCategoryIconImage(category.getIconImage());
        response.setCategoryName(commonService.getMessageSrc(category.getName(), locale));
        response.setDescription(commonService.getMessageSrc(response.getDescription(), locale));

        String log = doCreate ? actionLogExpenseCreate : actionLogExpenseUpdate;
        return responseFactory.success(log, response);
    }

    @Override
    public ResponseEntity<BaseResponse<ExpenseResponse>> detail(Long id, Locale locale) {
        Long currentUserId = commonService.getCurrentUserId();
        Optional<ExpenseResponse> optResponse = expenseRepository.accessibleById(id, currentUserId, ACTIVE);
        if (optResponse.isEmpty()) {
            commonService.throwException(EXPENSE_NOT_FOUND, locale, null);
        }

        ExpenseResponse response = optResponse.get();
        response.setWalletName(commonService.getMessageSrc(response.getWalletName(), locale));
        response.setCategoryName(commonService.getMessageSrc(response.getCategoryName(), locale));
        response.setDescription(commonService.getMessageSrc(response.getDescription(), locale));
        return responseFactory.success(null, response);
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<String>> delete(Long id, Locale locale) {
        Long currentUserId = commonService.getCurrentUserId();
        Expense expense = expenseRepository.findByIdAndOwner(id, currentUserId);
        if (expense == null) {
            commonService.throwException(EXPENSE_NOT_FOUND_OR_NOT_ACCESSIBLE, locale, null);
        }

        //clone sang history đã rồi làm gì thì làm
        ExpenseHistory history = expenseMapper.toHistory(expense);
        expenseHistoryRepository.save(history);

        Wallet wallet = walletRepository.findById(expense.getWalletId()).orElse(null);
        if (wallet == null) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        if (expense.getStatus().equals(ACTIVE) && !expense.getType().equals(SCHEDULE)) {
            resetOldAmountInWallet(expense, wallet);
        }
        expense.setUpdatedAt(getNow());
        expense.setStatus(DELETED);
        return responseFactory.success(actionLogExpenseDelete, "expense_delete.success", locale, null);
    }

    @Override
    public ResponseEntity<BaseResponse<ExpenseFilterResource>> getResourceForExpenseFilter(Long walletId, Locale locale) {
        CustomUserDetail currentUser = commonService.getCurrentUser();
        List<Wallet> wallets = walletRepository.findByUserId(currentUser.getId(), false);
        if (walletId == null) {
            walletId = wallets.stream().filter(Wallet::getIsDefault).findFirst().get().getId();
        }
        final Long finalWalletId = walletId;
        Optional<Wallet> otpWallet = wallets.stream().filter(w -> w.getId().equals(finalWalletId)).findFirst();
        if (otpWallet.isEmpty()) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        Set<Map<Long, String>> walletMap = wallets.stream()
                .map(w -> Map.of(w.getId(), walletService.getDisplayWalletName(w, currentUser.getId(), null, locale)))
                .collect(Collectors.toSet());

        ResponseEntity<BaseResponse<Set<ExpenseCategoryResponse>>>
                categories = categoryService.getAll(Constant.CategorySaveType.WALLET, walletId, locale);

        Set<Map<Long, String>> categoryMap = categories.getBody().getData()[0]
                .stream().map(c -> Map.of(c.getId(), c.getName()))
                .collect(Collectors.toSet());

        Set<Map<Long, String>> memberMap = userWalletRepository.findUserByWallet(finalWalletId)
                .stream().map(u -> Map.of(u.getId(), u.getName()))
                .collect(Collectors.toSet());

        return responseFactory.success(null,
                ExpenseFilterResource.builder()
                        .walletId(finalWalletId)
                        .walletName(walletService.getDisplayWalletName(otpWallet.get(), currentUser.getId(), null, locale))
                        .members(memberMap)
                        .otherWalletMap(walletMap)
                        .categories(categoryMap)
                .build()
        );
    }

    @Override
    public ResponseEntity<BaseResponse<List<ExpenseResponse>>> listByUser(Long walletId, String keyword,
                                                                          Long categoryId, Long createdby,
                                                                          String startTime, String endTime,
                                                                          Locale locale) {
        LocalDateTime startDateVal = clientToLocalDateTime(startTime);
        LocalDateTime endDateVal = clientToLocalDateTime(endTime);
        List<ExpenseResponse> list = expenseRepository.findAccessByUser(commonService.getCurrentUserId(), walletId, keyword, categoryId, createdby, startDateVal, endDateVal)
                .stream().peek(e -> {
                    e.setWalletName(commonService.getMessageSrc(e.getWalletName(), locale));
                    e.setCategoryName(commonService.getMessageSrc(e.getCategoryName(), locale));
                }).toList();
        return responseFactory.success(null, list);
    }

    @Override
    public ResponseEntity<BaseResponse<ExpenseEditResource>> getResourceForExpenseEdit(Long walletId, Locale locale) {
        CustomUserDetail currentUser = commonService.getCurrentUser();
        List<Wallet> wallets = walletRepository.findByUserId(currentUser.getId(), false);
        if (walletId == null) {
            walletId = wallets.stream().filter(Wallet::getIsDefault).findFirst().get().getId();
        }
        final Long finalWalletId = walletId;
        Optional<Wallet> otpWallet = wallets.stream().filter(w -> w.getId().equals(finalWalletId)).findFirst();
        if (otpWallet.isEmpty()) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        List<Map<Long, String>> walletMap = wallets.stream()
                .map(w -> Map.of(w.getId(), walletService.getDisplayWalletName(w, currentUser.getId(), null, locale)))
                .toList();

        ResponseEntity<BaseResponse<Set<ExpenseCategoryResponse>>>
                categories = categoryService.getAll(Constant.CategorySaveType.WALLET, walletId, locale);

        Set<ExpenseCategoryResponse> categoryResponses = categories.getBody().getData()[0];

        return responseFactory.success(null,
                ExpenseEditResource.builder()
                        .walletId(walletId)
                        .walletName(walletService.getDisplayWalletName(otpWallet.get(), currentUser.getId(), null, locale))
                        .otherWalletMap(walletMap)
                        .categories(categoryResponses)
                        .build()
                );
    }

    @Override
    @Transactional
    public void rollback(Long id, Locale locale) {
        Expense expense = expenseRepository.findById(id).orElse(null);
        if (expense == null) {
            commonService.throwException(EXPENSE_NOT_FOUND, locale, null);
        }
        if (expense.getStatus().equals(ACTIVE)) {
            commonService.throwException(EXPENSE_IS_ACTIVE, locale, null);
        }

        //lưu lịch sử lại cái đã
        ExpenseHistory history = expenseMapper.toHistory(expense);
        expenseHistoryRepository.save(history);

        Wallet wallet = walletRepository.findById(expense.getWalletId()).orElse(null);
        if (wallet == null) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        expense.setStatus(ACTIVE);
        amountDivision(expense, wallet);
    }

    @Override
    @Transactional
    public void executeFromScheduler(TaskSchedulerConfig config, Locale locale, LocalDateTime now) {
        Expense expenseOrg = expenseRepository.findByIdScheduled(config.getRefId()).orElse(null);

        if (expenseOrg != null) {
            Wallet walletOrg = walletRepository.findById(expenseOrg.getWalletId()).orElse(null);
            if (walletOrg != null) {
                Expense expense = expenseMapper.cloneToNew(expenseOrg);
                expense.setType(FROM_SCHEDULE);
                expense.setStatus(ACTIVE);
                expense.setEntryDate(now);
                expense.setUpdatedAt(now);
                expense.setUpdatedBy(0L);
                expense.setCreatedAt(now);
                expense.setCreatedBy(0L);
                expenseRepository.save(expense); //persist để lấy ID nếu cần

                amountDivision(expense, walletOrg);
                warningChecker(walletOrg, locale, expenseOrg.getCreatedBy());
                walletRepository.save(walletOrg);
            }
        }
    }

    private void resetOldAmountInWallet(Expense expense, Wallet wallet) {
        //khôi phục số dư ví khi chưa thêm expense
        BigDecimal oldBalance;
        if (expense.getEntryType().equals(INCOME)) {
            oldBalance = wallet.getBalance().subtract(expense.getAmount()); //trừ đi số tiền đã thêm vào
            wallet.setTotalIncome(wallet.getTotalIncome().subtract(expense.getAmount())); //trừ đi số tiền đã thêm vào income
        } else {
            oldBalance = wallet.getBalance().add(expense.getAmount()); //cộng lại số tiền đã bị trừ bởi chi tiêu này
            wallet.setTotalExpense(wallet.getTotalExpense().subtract(expense.getAmount())); //trừ đi số tiền đã thêm vào expense
        }

        wallet.setBalance(oldBalance);
    }

    private void amountDivision(Expense expense, Wallet wallet) {
        BigDecimal newBalance;
        if (expense.getEntryType().equals(INCOME)) {
            newBalance = wallet.getBalance().add(expense.getAmount());
            wallet.setTotalIncome(wallet.getTotalIncome().add(expense.getAmount()));
        } else {
            newBalance = wallet.getBalance().subtract(expense.getAmount());
            wallet.setTotalExpense(wallet.getTotalExpense().add(expense.getAmount()));
        }

        expense.setNewBalance(newBalance);
        wallet.setBalance(newBalance);
    }
}

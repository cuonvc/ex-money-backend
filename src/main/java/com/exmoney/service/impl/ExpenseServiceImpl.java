package com.exmoney.service.impl;

import com.exmoney.entity.*;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.mapper.ExpenseMapper;
import com.exmoney.payload.mapper.WalletMapper;
import com.exmoney.payload.request.expense.ExpenseRequest;
import com.exmoney.payload.response.expense.ExpenseEditResource;
import com.exmoney.payload.response.expense.ExpenseFilterResource;
import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.payload.response.expenseCategory.ExpenseCategoryResponse;
import com.exmoney.payload.response.wallet.WalletResponse;
import com.exmoney.repository.*;
import com.exmoney.security.CustomUserDetail;
import com.exmoney.service.CommonService;
import com.exmoney.service.ExpenseCategoryService;
import com.exmoney.service.ExpenseService;
import com.exmoney.service.WalletService;
import com.exmoney.util.Constant;
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
import static com.exmoney.util.Constant.ExpenseEntryType.*;
import static com.exmoney.util.Constant.ExpenseType.EXPENSE_TYPES;
import static com.exmoney.util.Constant.ExpenseType.MANUAL;
import static com.exmoney.util.Constant.Status.ACTIVE;
import static com.exmoney.util.Constant.Status.PENDING;
import static com.exmoney.util.Utils.clientToLocalDateTime;
import static com.exmoney.util.Utils.getNow;

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

    @Value("${exmoney.application.default.expense_income_name}")
    private String expenseIncomeName;

    @Value("${exmoney.application.default.expense_income_description}")
    private String expenseIncomeDescription;

    @Value("${exmoney.application.action_log.expense_create}")
    private String actionLogExpenseCreate;

    @Value("${exmoney.application.action_log.expense_update}")
    private String actionLogExpenseUpdate;

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<ExpenseResponse>> create(ExpenseRequest request, Locale locale) {

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
        }
        if (!EXPENSE_TYPES.contains(request.getType())) {
            commonService.throwException(INTERNAL_SERVER_ERROR, locale, null);
        }
        expense.setStatus(request.getType().equals(MANUAL) ? ACTIVE : PENDING);

        expense = expenseRepository.save(expense);
        wallet = walletRepository.save(wallet);

        return doResponse(expense, wallet, optCategory.get(), userDetail, locale, true);
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<ExpenseResponse>> update(Long id, ExpenseRequest request, Locale locale) {
        CustomUserDetail userDetail = commonService.getCurrentUser();
        Long currentUserId = userDetail.getId();
        Expense expense = expenseRepository.findByIdAndOwner(id, currentUserId);
        if (expense == null) {
            commonService.throwException(EXPENSE_NOT_FOUND, locale, null);
        }

        Wallet wallet = walletRepository.findById(expense.getWalletId()).orElse(null);
        if (wallet == null) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        Optional<ExpenseCategory> optCategory = categoryRepository
                .findByIdAndAccess(request.getCategoryId(), request.getWalletId(), currentUserId);
        if (optCategory.isEmpty()) {
            commonService.throwException(CATEGORY_NOT_FOUND, locale, null, request.getCategoryId());
        }

        ExpenseHistory history = expenseMapper.toHistory(expense);
        expenseHistoryRepository.save(history);

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

        amountDivision(expense, wallet); //update lại số tiền
        expense = expenseRepository.save(expense);
        wallet = walletRepository.save(wallet);

        return doResponse(expense, wallet, optCategory.get(), userDetail, locale, false);
    }

    private ResponseEntity<BaseResponse<ExpenseResponse>> doResponse(Expense expense, Wallet wallet,
                                                                     ExpenseCategory category, CustomUserDetail userDetail,
                                                                     Locale locale, boolean doCreate) {
        ExpenseResponse response = expenseMapper.toResponse(expense);
        response.setWalletName(commonService.getMessageSrc(wallet.getName(), locale));
        response.setUserName(userDetail.getName());
        response.setCategoryName(commonService.getMessageSrc(category.getName(), locale));
        response.setDescription(commonService.getMessageSrc(response.getDescription(), locale));

        String log = doCreate ? actionLogExpenseCreate : actionLogExpenseUpdate;
        return responseFactory.success(log, response);
    }

    private void resetOldAmountInWallet(Expense expense, Wallet wallet) {
        //khôi phục số dư ví khi chưa thêm expense
        BigDecimal oldBalance;
        if (expense.getEntryType().equals(INCOME)) {
            oldBalance = wallet.getBalance().subtract(expense.getAmount());
            wallet.setTotalIncome(wallet.getTotalIncome().subtract(expense.getAmount()));
        } else {
            oldBalance = wallet.getBalance().add(expense.getAmount());
            wallet.setTotalExpense(wallet.getTotalExpense().subtract(expense.getAmount()));
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

    @Override
    public ResponseEntity<BaseResponse<ExpenseResponse>> detail(Long id, Locale locale) {
        Long currentUserId = commonService.getCurrentUserId();
        Optional<ExpenseResponse> optResponse = expenseRepository.accessibleById(id, currentUserId);
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
                                                                          Long categoryId, Long createdby, Locale locale) {
        List<ExpenseResponse> list = expenseRepository.findAccessByUser(commonService.getCurrentUserId(), walletId, keyword, categoryId, createdby)
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
}

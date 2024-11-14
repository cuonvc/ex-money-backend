package com.exmoney.service.impl;

import com.exmoney.entity.Expense;
import com.exmoney.entity.ExpenseCategory;
import com.exmoney.entity.Wallet;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.mapper.ExpenseMapper;
import com.exmoney.payload.mapper.WalletMapper;
import com.exmoney.payload.request.expense.ExpenseRequest;
import com.exmoney.payload.response.expense.ExpenseEditResource;
import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.payload.response.expenseCategory.ExpenseCategoryResponse;
import com.exmoney.payload.response.wallet.WalletResponse;
import com.exmoney.repository.ExpenseCategoryRepository;
import com.exmoney.repository.ExpenseRepository;
import com.exmoney.repository.WalletRepository;
import com.exmoney.security.CustomUserDetail;
import com.exmoney.service.CommonService;
import com.exmoney.service.ExpenseCategoryService;
import com.exmoney.service.ExpenseService;
import com.exmoney.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.exmoney.payload.enumerate.ErrorCode.*;
import static com.exmoney.util.Constant.ExpenseEntryType.ENTRY_TYPES;
import static com.exmoney.util.Constant.ExpenseEntryType.INCOME;
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

    @Value("${exmoney.application.default.expense_income_name}")
    private String expenseIncomeName;

    @Value("${exmoney.application.default.expense_income_description}")
    private String expenseIncomeDescription;

    @Value("${exmoney.application.action_log.expense_create}")
    private String actionLogExpenseCreate;

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<ExpenseResponse>> create(ExpenseRequest request, Locale locale) {

        CustomUserDetail userDetail = commonService.getCurrentUser();
        String currentUserId = userDetail.getId();
        Optional<Wallet> optWallet = walletRepository.findByIdAndUser(request.getWalletId(), currentUserId);
        if (optWallet.isEmpty()) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        LocalDateTime entryDate;
        if (request.getEntryDate() == null) {
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
        expense.setEntryDate(entryDate);
        expense.setCreatedAt(getNow());
        expense.setCreatedBy(currentUserId);
        expense.setUserId(currentUserId);
        amountDivision(expense, optWallet.get());
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
        ExpenseResponse response = expenseMapper.toResponse(expenseRepository.save(expense));
        response.setWalletName(commonService.getMessageSrc(optWallet.get().getName(), locale));
        response.setUserName(userDetail.getName());
        response.setCategoryName(commonService.getMessageSrc(optCategory.get().getName(), locale));
        response.setDescription(commonService.getMessageSrc(response.getDescription(), locale));
        return responseFactory.success(actionLogExpenseCreate, response);
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
    public ResponseEntity<BaseResponse<ExpenseResponse>> detail(String id, Locale locale) {
        String currentUserId = commonService.getCurrentUserId();
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
    public ResponseEntity<BaseResponse<List<ExpenseResponse>>> listByUser(String walletId, Locale locale) {
        List<ExpenseResponse> list = expenseRepository.findAccessByUser(commonService.getCurrentUserId(), walletId)
                .stream().peek(e -> {
                    e.setWalletName(commonService.getMessageSrc(e.getWalletName(), locale));
                    e.setCategoryName(commonService.getMessageSrc(e.getCategoryName(), locale));
                }).toList();
        return responseFactory.success(null, list);
    }

    @Override
    public ResponseEntity<BaseResponse<ExpenseEditResource>> getResourceForExpenseEdit(String walletId, Locale locale) {
        String currentUserId = commonService.getCurrentUserId();
        List<Wallet> wallets = walletRepository.findByUserId(currentUserId, false);
        if (walletId == null || walletId.isEmpty()) {
            walletId = wallets.stream().filter(Wallet::getIsDefault).findFirst().get().getId();
        }
        final String finalWalletId = walletId;
        Optional<Wallet> otpWallet = wallets.stream().filter(w -> w.getId().equals(finalWalletId)).findFirst();
        if (otpWallet.isEmpty()) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        List<Map<String, String>> walletMap = wallets.stream()
                .map(w -> Map.of(w.getId(), w.getName()))
                .toList();

        ResponseEntity<BaseResponse<Set<ExpenseCategoryResponse>>>
                categories = categoryService.getAll(Constant.CategorySaveType.WALLET, walletId, locale);

        Set<ExpenseCategoryResponse> categoryResponses = categories.getBody().getData()[0];

        return responseFactory.success(null,
                ExpenseEditResource.builder()
                        .walletId(walletId)
                        .walletName(otpWallet.get().getName())
                        .otherWalletMap(walletMap)
                        .categories(categoryResponses)
                        .build()
                );
    }
}

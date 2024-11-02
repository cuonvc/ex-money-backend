package com.exmoney.service.impl;

import com.exmoney.entity.Expense;
import com.exmoney.entity.ExpenseCategory;
import com.exmoney.entity.Wallet;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.mapper.ExpenseMapper;
import com.exmoney.payload.request.expense.ExpenseRequest;
import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.repository.ExpenseCategoryRepository;
import com.exmoney.repository.ExpenseRepository;
import com.exmoney.repository.WalletRepository;
import com.exmoney.security.CustomUserDetail;
import com.exmoney.service.CommonService;
import com.exmoney.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.exmoney.payload.enumerate.ErrorCode.*;
import static com.exmoney.util.Constant.ExpenseType.MANUAL;
import static com.exmoney.util.Constant.Status.ACTIVE;
import static com.exmoney.util.Constant.Status.PENDING;
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

    @Value("${exmoney.application.action_log.expense_create}")
    private String actionLogExpenseCreate;

    @Override
    public ResponseEntity<BaseResponse<ExpenseResponse>> create(ExpenseRequest request, Locale locale) {

        CustomUserDetail userDetail = commonService.getCurrentUser();
        String currentUserId = userDetail.getId();
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
        expense.setCreatedAt(getNow());
        expense.setCreatedBy(currentUserId);
        expense.setUserId(currentUserId);
        expense.setStatus(request.getType().equals(MANUAL) ? ACTIVE : PENDING);
        ExpenseResponse response = expenseMapper.toResponse(expenseRepository.save(expense));
        response.setWalletName(optWallet.get().getName());
        response.setUserName(userDetail.getName());
        response.setCategoryName(optCategory.get().getName());
        return responseFactory.success(actionLogExpenseCreate, response);
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
}

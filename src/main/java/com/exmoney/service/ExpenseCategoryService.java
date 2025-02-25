package com.exmoney.service;

import com.exmoney.entity.ExpenseCategory;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.expenseCategory.ExpenseCategoryRequest;
import com.exmoney.payload.response.expenseCategory.ExpenseCategoryResponse;
import org.springframework.http.ResponseEntity;

import java.util.Locale;
import java.util.Set;

public interface ExpenseCategoryService {
    ResponseEntity<BaseResponse<ExpenseCategory>> createDefaultForAdmin(String name, String desc, Locale locale);
    ResponseEntity<BaseResponse<ExpenseCategoryResponse>> create(ExpenseCategoryRequest request, Locale locale);
    ResponseEntity<BaseResponse<ExpenseCategoryResponse>> update(Long id, ExpenseCategoryRequest request, Locale locale);
    ResponseEntity<BaseResponse<Boolean>> delete(Long id, Locale locale);
    ResponseEntity<BaseResponse<Set<ExpenseCategoryResponse>>> getAll(Long walletId, Locale locale);
    ResponseEntity<BaseResponse<Set<String>>> getAllDefault(Locale locale);
    ResponseEntity<BaseResponse<ExpenseCategory>> detail(Long id, Locale locale);
}

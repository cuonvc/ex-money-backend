package com.exmoney.service;

import com.exmoney.entity.ExpenseCategory;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.expenseCategory.ExpenseCategoryRequest;
import com.exmoney.payload.response.expenseCategory.ExpenseCategoryResponse;
import org.springframework.http.ResponseEntity;

import java.util.Locale;
import java.util.Set;

public interface ExpenseCategoryService {
    ResponseEntity<BaseResponse<ExpenseCategory>> create(ExpenseCategoryRequest request, Locale locale);
    ResponseEntity<BaseResponse<ExpenseCategory>> update(String id, ExpenseCategoryRequest request, Locale locale);
    ResponseEntity<BaseResponse<Set<ExpenseCategoryResponse>>> getAll(String saveType, String refId, Locale locale);
    ResponseEntity<BaseResponse<ExpenseCategory>> detail(String id, Locale locale);
}

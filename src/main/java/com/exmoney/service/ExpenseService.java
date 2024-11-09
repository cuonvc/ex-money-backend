package com.exmoney.service;

import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.expense.ExpenseRequest;
import com.exmoney.payload.response.expense.ExpenseEditResource;
import com.exmoney.payload.response.expense.ExpenseResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Locale;

public interface ExpenseService {
    ResponseEntity<BaseResponse<ExpenseResponse>> create(ExpenseRequest request, Locale locale);
    ResponseEntity<BaseResponse<ExpenseResponse>> detail(String id, Locale locale);
    ResponseEntity<BaseResponse<List<ExpenseResponse>>> listByUser(String walletId, Locale locale);
    ResponseEntity<BaseResponse<ExpenseEditResource>> getResourceForExpenseEdit(String walletId, Locale locale);
}

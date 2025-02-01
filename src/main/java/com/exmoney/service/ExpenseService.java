package com.exmoney.service;

import com.exmoney.entity.TaskSchedulerConfig;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.expense.ExpenseCreateRequest;
import com.exmoney.payload.request.expense.ExpenseUpdateRequest;
import com.exmoney.payload.response.expense.ExpenseConfirmResponse;
import com.exmoney.payload.response.expense.ExpenseEditResource;
import com.exmoney.payload.response.expense.ExpenseFilterResource;
import com.exmoney.payload.response.expense.ExpenseResponse;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public interface ExpenseService {
    ResponseEntity<BaseResponse<ExpenseResponse>> create(ExpenseCreateRequest request, Locale locale);
    ResponseEntity<BaseResponse<ExpenseResponse>> update(Long id, ExpenseUpdateRequest request, Locale locale);

    ResponseEntity<BaseResponse<ExpenseConfirmResponse>> suggestFromSpeech(String textFromSpeech, Locale locale);
    ResponseEntity<BaseResponse<ExpenseResponse>> detail(Long id, Locale locale);
    ResponseEntity<BaseResponse<String>> delete(Long id, Locale locale);
    ResponseEntity<BaseResponse<ExpenseFilterResource>> getResourceForExpenseFilter(Long walletId, Locale locale);
    ResponseEntity<BaseResponse<List<ExpenseResponse>>> listByUser(Long walletId, String keyword, Long categoryId, Long createdBy, String startTime, String endTime, Locale locale);
    ResponseEntity<BaseResponse<ExpenseEditResource>> getResourceForExpenseEdit(Long walletId, Locale locale);
    void rollback(Long id, Locale locale);
    void executeFromScheduler(TaskSchedulerConfig config, Locale locale, LocalDateTime now);
}

package com.exmoney.service;

import com.exmoney.entity.TaskSchedulerConfig;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.scheduler.ExpenseSchedulerRequest;
import com.exmoney.payload.response.scheduler.SchedulerResponse;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

public interface TaskSchedulerService {

    ResponseEntity<BaseResponse<SchedulerResponse>> create(ExpenseSchedulerRequest request, Locale locale);
    ResponseEntity<BaseResponse<SchedulerResponse>> update(Long id, ExpenseSchedulerRequest request, Locale locale);

    String getIntervalMessage(String type, int val, Locale locale);
}

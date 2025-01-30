package com.exmoney.controller;

import com.exmoney.entity.TaskSchedulerConfig;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.scheduler.ExpenseSchedulerRequest;
import com.exmoney.payload.response.scheduler.SchedulerResponse;
import com.exmoney.service.TaskSchedulerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

import static com.exmoney.util.Constant.API_BASE_USER;

@RestController
@RequiredArgsConstructor
public class TaskSchedulerController {

    private final TaskSchedulerService taskSchedulerService;

    @PostMapping(API_BASE_USER + "/task/expense_scheduler")
    public ResponseEntity<BaseResponse<SchedulerResponse>> create(@RequestBody @Valid ExpenseSchedulerRequest request,
                                                                    @RequestParam Locale locale) {
        return taskSchedulerService.create(request, locale);
    }

    @PutMapping(API_BASE_USER + "/task/expense_scheduler/{id}")
    public ResponseEntity<BaseResponse<SchedulerResponse>> update(@PathVariable Long id,
                                                                    @RequestBody @Valid ExpenseSchedulerRequest request,
                                                                    @RequestParam Locale locale) {
        return taskSchedulerService.update(id, request, locale);
    }
}

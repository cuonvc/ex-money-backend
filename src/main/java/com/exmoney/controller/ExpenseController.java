package com.exmoney.controller;

import com.exmoney.entity.Expense;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.expense.ExpenseRequest;
import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

import static com.exmoney.util.Constant.API_BASE_USER;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping(API_BASE_USER + "/expense")
    public ResponseEntity<BaseResponse<ExpenseResponse>> create(@Valid @RequestBody ExpenseRequest request,
                                                                @RequestParam Locale locale) {
        return expenseService.create(request, locale);
    }

    @GetMapping(API_BASE_USER + "/expense/{id}")
    public ResponseEntity<BaseResponse<ExpenseResponse>> detail(@PathVariable("id") String id,
                                                        @RequestParam Locale locale) {
        return expenseService.detail(id, locale);
    }

    @GetMapping(API_BASE_USER + "/expense")
    public ResponseEntity<BaseResponse<List<ExpenseResponse>>> list(@RequestParam(value = "wallet_id", required = false) String walletId,
                                                                    @RequestParam Locale locale) {
        return expenseService.listByUser(walletId, locale);
    }
}

package com.exmoney.controller;

import com.exmoney.entity.Expense;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.expense.ExpenseRequest;
import com.exmoney.payload.response.expense.ExpenseEditResource;
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
    public ResponseEntity<BaseResponse<ExpenseResponse>> detail(@PathVariable("id") Long id,
                                                        @RequestParam Locale locale) {
        return expenseService.detail(id, locale);
    }

    @GetMapping(API_BASE_USER + "/expense") //walletId is null or blank -> get by default wallet
    public ResponseEntity<BaseResponse<List<ExpenseResponse>>> list(@RequestParam(value = "wallet_id", required = false) Long walletId,
                                                                    @RequestParam(value = "keyword", required = false) String keyword,
                                                                    @RequestParam(value = "category_id", required = false) Long categoryId,
                                                                    @RequestParam(value = "created_by", required = false) Long createdBy,
                                                                    @RequestParam Locale locale) {
        return expenseService.listByUser(walletId, keyword, categoryId, createdBy, locale);
    }

    @GetMapping(API_BASE_USER + "/expense/edit_resource")
    //get resource for expense edit screen
    public ResponseEntity<BaseResponse<ExpenseEditResource>> getResourceForExpenseEdit(@RequestParam(value = "wallet_id", required = false) Long walletId,
                                                                                             @RequestParam Locale locale) {
        return expenseService.getResourceForExpenseEdit(walletId, locale);
    }
}

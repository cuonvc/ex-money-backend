package com.exmoney.controller;

import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.expense.ExpenseCreateRequest;
import com.exmoney.payload.request.expense.ExpenseUpdateRequest;
import com.exmoney.payload.response.expense.ExpenseEditResource;
import com.exmoney.payload.response.expense.ExpenseFilterResource;
import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

import static com.exmoney.util.Constant.API_BASE_ADMIN;
import static com.exmoney.util.Constant.API_BASE_USER;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping(API_BASE_USER + "/expense")
    public ResponseEntity<BaseResponse<ExpenseResponse>> create(@Valid @RequestBody ExpenseCreateRequest request,
                                                                @RequestParam Locale locale) {
        return expenseService.create(request, locale);
    }

    @PutMapping(API_BASE_USER + "/expense/{id}")
    public ResponseEntity<BaseResponse<ExpenseResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody ExpenseUpdateRequest request,
                                                                @RequestParam Locale locale) {
        return expenseService.update(id, request, locale);
    }

    @GetMapping(API_BASE_USER + "/expense/{id}")
    public ResponseEntity<BaseResponse<ExpenseResponse>> detail(@PathVariable("id") Long id,
                                                                @RequestParam Locale locale) {
        return expenseService.detail(id, locale);
    }

    @DeleteMapping(API_BASE_USER + "/expense/{id}")
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable("id") Long id,
                                                                @RequestParam Locale locale) {
        return expenseService.delete(id, locale);
    }

    @GetMapping(API_BASE_USER + "/expense/filter_resource")
    public ResponseEntity<BaseResponse<ExpenseFilterResource>> getResourceForExpenseFilter(@RequestParam(value = "wallet_id", required = false) Long walletId,
                                                                                           @RequestParam Locale locale) {
        return expenseService.getResourceForExpenseFilter(walletId, locale);
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

    @PutMapping(API_BASE_ADMIN + "/expense/rollback/{id}")
    public ResponseEntity rollback(@PathVariable Long id, @RequestParam Locale locale) {
        expenseService.rollback(id, locale);
        return ResponseEntity.ok().build();
    }
}

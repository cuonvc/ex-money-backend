package com.exmoney.controller;

import com.exmoney.entity.ExpenseCategory;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.expenseCategory.ExpenseCategoryRequest;
import com.exmoney.payload.response.expenseCategory.ExpenseCategoryResponse;
import com.exmoney.service.ExpenseCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Set;

import static com.exmoney.util.Constant.API_BASE_ADMIN;
import static com.exmoney.util.Constant.API_BASE_USER;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    //thêm ở đây thì phải thêm cả ở file yml + file properties + application nữa
    @PostMapping(API_BASE_ADMIN + "/category")
    public ResponseEntity<BaseResponse<ExpenseCategory>> createDefault(@RequestParam String name,
                                                                       @RequestParam String description,
                                                                       @RequestParam Locale locale) {
        return expenseCategoryService.createDefaultForAdmin(name, description, locale);
    }

    @PostMapping(API_BASE_USER + "/category")
    public ResponseEntity<BaseResponse<ExpenseCategoryResponse>> create(@RequestParam Locale locale,
                                                                @RequestBody @Valid ExpenseCategoryRequest request) {
        return expenseCategoryService.create(request, locale);
    }

    @PutMapping(API_BASE_USER + "/category/{id}")
    public ResponseEntity<BaseResponse<ExpenseCategoryResponse>> update(@RequestParam Locale locale,
                                                                @PathVariable Long id,
                                                                @RequestBody @Valid ExpenseCategoryRequest request) {
        return expenseCategoryService.update(id, request, locale);
    }

    @DeleteMapping(API_BASE_USER + "/category/{id}")
    public ResponseEntity<BaseResponse<Boolean>> delete(@RequestParam Locale locale,
                                                        @PathVariable Long id) {
        return expenseCategoryService.delete(id, locale);
    }

    @GetMapping(API_BASE_USER + "/category")
    public ResponseEntity<BaseResponse<Set<ExpenseCategoryResponse>>> getAll(@RequestParam Locale locale,
                                                                             @RequestParam(required = false) Long walletId,
                                                                             @RequestParam(required = false) String keyword) {
        return expenseCategoryService.getAll(walletId, keyword, locale);
    }

    @GetMapping(API_BASE_USER + "/category/default-name-test")
    public ResponseEntity<BaseResponse<Set<String>>> getDefault(@RequestParam Locale locale) {
        return expenseCategoryService.getAllDefault(locale);
    }

    //detail không cần get children
    @GetMapping(API_BASE_USER + "/category/{id}")
    public ResponseEntity<BaseResponse<ExpenseCategory>> detail(@RequestParam Locale locale,
                                                                @PathVariable Long id) {
        return expenseCategoryService.detail(id, locale);
    }
}

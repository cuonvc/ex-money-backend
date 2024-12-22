package com.exmoney.controller;

import com.exmoney.entity.ExpenseCategory;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.request.expenseCategory.ExpenseCategoryRequest;
import com.exmoney.payload.response.expenseCategory.ExpenseCategoryResponse;
import com.exmoney.service.ExpenseCategoryService;
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
    public ResponseEntity<BaseResponse<ExpenseCategory>> create(@RequestParam Locale locale,
                                                                @RequestBody ExpenseCategoryRequest request) {
        return expenseCategoryService.create(request, locale);
    }

    @PutMapping(API_BASE_USER + "/category/{id}")
    public ResponseEntity<BaseResponse<ExpenseCategory>> update(@RequestParam Locale locale,
                                                                @PathVariable Long id,
                                                                @RequestBody ExpenseCategoryRequest request) {
        return expenseCategoryService.update(id, request, locale);
    }

    @GetMapping(API_BASE_USER + "/category")
    public ResponseEntity<BaseResponse<Set<ExpenseCategoryResponse>>> getAll(@RequestParam Locale locale,
                                                                             @RequestParam(value = "save_type", required = false) String saveType,
                                                                             @RequestParam(value = "ref_id", required = false) Long refId) {
        return expenseCategoryService.getAll(saveType, refId, locale);
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

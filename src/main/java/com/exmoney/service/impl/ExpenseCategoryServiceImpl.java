package com.exmoney.service.impl;

import com.exmoney.entity.ExpenseCategory;
import com.exmoney.entity.Wallet;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.mapper.ExpenseCategoryMapper;
import com.exmoney.payload.request.expenseCategory.ExpenseCategoryRequest;
import com.exmoney.payload.response.expenseCategory.ExpenseCategoryResponse;
import com.exmoney.repository.ExpenseCategoryRepository;
import com.exmoney.repository.WalletRepository;
import com.exmoney.service.CommonService;
import com.exmoney.service.ExpenseCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.exmoney.payload.enumerate.ErrorCode.*;
import static com.exmoney.util.Constant.CategorySaveType.ACCOUNT;
import static com.exmoney.util.Constant.CategorySaveType.WALLET;
import static com.exmoney.util.Constant.RecordType.CUSTOM;
import static com.exmoney.util.Constant.Role.USER_ROLE;
import static com.exmoney.util.Utils.getNow;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final ExpenseCategoryMapper categoryMapper;
    private final ExpenseCategoryRepository categoryRepository;
    private final WalletRepository walletRepository;
    private final ResponseFactory responseFactory;
    private final CommonService commonService;

    @Value("${exmoney.application.action_log.expense_category_create}")
    private String categoryCreateLog;

    @Value("${exmoney.application.action_log.expense_category_update}")
    private String categoryUpdateLog;

    @Override
    public ResponseEntity<BaseResponse<ExpenseCategory>> create(ExpenseCategoryRequest request, Locale locale) {

        String currentUserId = commonService.getCurrentUserId();

        if (request.getParentId() != null && categoryRepository.findById(request.getParentId()).isEmpty()) {
            commonService.throwException(CATEGORY_NOT_FOUND, locale, null, request.getParentId());
        }

        if (!List.of(ACCOUNT, WALLET).contains(request.getSaveType())) {
            commonService.throwException(INTERNAL_SERVER_ERROR, locale, null, request.getSaveType());
        } else if (request.getSaveType().equals(ACCOUNT)) {
            request.setRefId(currentUserId);
            if (categoryRepository.findByNameAndUserId(request.getName(), currentUserId).isPresent()) {
                commonService.throwException(CATEGORY_NAME_EXISTED, locale, null, request.getName());
            }
        } else if (request.getSaveType().equals(WALLET)
                && walletRepository.findByIdAndUser(request.getRefId(), currentUserId).isEmpty()) {
            commonService.throwException(WALLET_NOT_FOUND, locale, null);
        }

        ExpenseCategory category = categoryMapper.toEntity(request);
        category.setCreatedAt(getNow());
        category.setCreatedBy(USER_ROLE);
        category.setType(CUSTOM);
        return responseFactory.success(categoryCreateLog, categoryRepository.save(category));
    }

    @Override
    public ResponseEntity<BaseResponse<ExpenseCategory>> update(String id, ExpenseCategoryRequest request, Locale locale) {

        Optional<ExpenseCategory> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            commonService.throwException(CATEGORY_NOT_FOUND, locale, null, request.getParentId());
        }

        ExpenseCategory entity = category.get();
        if (!entity.getType().equals(CUSTOM)) {
            commonService.throwException(CATEGORY_DEFAULT_CANNOT_UPDATE, locale, null, entity.getName());
        }

        String currentUserId = commonService.getCurrentUserId();
        Optional<ExpenseCategory> existedCategory = categoryRepository.findByNameAndUserId(request.getName(), currentUserId);
        //nếu cập nhật tên trùng với tên hiện tại của chính nó thì ko sao
        if (existedCategory.isPresent() && !existedCategory.get().getId().equals(entity.getId())) {
            commonService.throwException(CATEGORY_NAME_EXISTED, locale, null, request.getName());
        }

        entity = categoryMapper.toEntity(request, entity); //mapping ignore refId and saveType filed
        entity.setUpdatedAt(getNow());
        return responseFactory.success(categoryUpdateLog, categoryRepository.save(entity));
    }

    @Override
    public ResponseEntity<BaseResponse<Set<ExpenseCategoryResponse>>> getAll(String saveType, String refId, Locale locale) {
        Set<ExpenseCategoryResponse> result = new HashSet<>();
        if (saveType == null || !List.of(ACCOUNT, WALLET).contains(saveType)) {
            saveType = null;
            refId = null;
        } else if (saveType.equals(ACCOUNT)) {
            refId = commonService.getCurrentUserId();
        }

        categoryRepository.findAllParentByRefIdAndSaveType(refId, saveType)
                .forEach(parent -> {
                    ExpenseCategoryResponse response = categoryMapper.entityToResponse(parent);
                    fetchChildren(response);
                    result.add(response);
                });

        return responseFactory.success(null, result);
    }

    private void fetchChildren(ExpenseCategoryResponse response) {
        Set<ExpenseCategoryResponse> children = new HashSet<>();
        categoryRepository.findAllByParentId(response.getId())
                .forEach(child -> {
                    ExpenseCategoryResponse subResponse = categoryMapper.entityToResponse(child);
                    fetchChildren(subResponse);
                    children.add(subResponse);
                });
        response.setChildren(children);
    }

    public ResponseEntity<BaseResponse<ExpenseCategory>> detail(String id, Locale locale) {
        Optional<ExpenseCategory> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            commonService.throwException(CATEGORY_NOT_FOUND, locale, null, id);
        }
        return responseFactory.success(null, category.get());
    }
}

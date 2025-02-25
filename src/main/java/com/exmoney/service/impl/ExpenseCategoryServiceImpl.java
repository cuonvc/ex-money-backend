package com.exmoney.service.impl;

import com.exmoney.config.DefaultCategoryConfiguration;
import com.exmoney.entity.ExpenseCategory;
import com.exmoney.entity.Wallet;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.enumerate.ErrorCode;
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
import java.util.stream.Collectors;

import static com.exmoney.payload.enumerate.ErrorCode.*;
import static com.exmoney.util.Constant.CategorySaveType.ACCOUNT;
import static com.exmoney.util.Constant.CategorySaveType.WALLET;
import static com.exmoney.util.Constant.RecordType.CUSTOM;
import static com.exmoney.util.Constant.RecordType.DEFAULT;
import static com.exmoney.util.Constant.Role.ADMIN_ROLE;
import static com.exmoney.util.Constant.Role.USER_ROLE;
import static com.exmoney.util.Constant.Status.DELETED;
import static com.exmoney.util.Utils.getNow;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final ExpenseCategoryMapper categoryMapper;
    private final ExpenseCategoryRepository categoryRepository;
    private final WalletRepository walletRepository;
    private final ResponseFactory responseFactory;
    private final CommonService commonService;
    private final DefaultCategoryConfiguration defaultCategoryConfiguration;

    @Value("${exmoney.application.action_log.expense_category_create}")
    private String categoryCreateLog;

    @Value("${exmoney.application.action_log.expense_category_update}")
    private String categoryUpdateLog;

    @Value("${exmoney.application.action_log.expense_category_delete}")
    private String categoryDeleteLog;

    @Override
    public ResponseEntity<BaseResponse<ExpenseCategory>> createDefaultForAdmin(String name, String desc, Locale locale) {
        if (commonService.getCurrentUser().getAuthorities().stream().filter(r -> r.getAuthority().equals(ADMIN_ROLE)).toList().isEmpty()) {
            commonService.throwException(BAD_REQUEST, locale, "");
        }
        ExpenseCategory category = new ExpenseCategory();
        category.setCreatedBy(0L);
        category.setName(name);
        category.setDescription(desc);
        category.setParentId(null);
        category.setType(DEFAULT);
        category.setUpdatedAt(getNow());
        categoryRepository.save(category);
        return responseFactory.success("", category);
    }

    @Override
    public ResponseEntity<BaseResponse<ExpenseCategoryResponse>> create(ExpenseCategoryRequest request, Locale locale) {

        Long currentUserId = commonService.getCurrentUserId();

        if (request.getParentId() != null && categoryRepository.findById(request.getParentId()).isEmpty()) {
            commonService.throwException(CATEGORY_NOT_FOUND, locale, null, request.getParentId());
        }

        if (!List.of(ACCOUNT, WALLET).contains(request.getSaveType())) {
            commonService.throwException(INTERNAL_SERVER_ERROR, locale, null, request.getSaveType());
        } else if (request.getSaveType().equals(ACCOUNT)) {
            request.setRefId(currentUserId);
            if (categoryRepository.findByNameAndUserId(request.getName(), currentUserId).isPresent()) {
                commonService.throwException(CATEGORY_NAME_EXISTED_BY_ACCOUNT, locale, null, request.getName());
            }
        } else if (request.getSaveType().equals(WALLET)) {
            if (walletRepository.findByIdAndUser(request.getRefId(), currentUserId).isEmpty()) {
                commonService.throwException(WALLET_NOT_FOUND, locale, null);
            }

            if (!categoryRepository.findByNameAndWalletId(request.getName(), request.getRefId()).isEmpty()) {
                commonService.throwException(CATEGORY_NAME_EXISTED_BY_WALLET, locale, null, request.getName());
            }
        }

        ExpenseCategory category = categoryMapper.toEntity(request);
        category.setCreatedAt(getNow());
        category.setCreatedBy(currentUserId);
        category.setType(CUSTOM);
        return responseFactory.success(categoryCreateLog, categoryMapper.entityToResponse(categoryRepository.save(category)));
    }

    @Override
    public ResponseEntity<BaseResponse<ExpenseCategoryResponse>> update(Long id, ExpenseCategoryRequest request, Locale locale) {

        Optional<ExpenseCategory> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            commonService.throwException(CATEGORY_NOT_FOUND, locale, null, request.getParentId());
        }

        ExpenseCategory entity = category.get();
        if (!entity.getType().equals(CUSTOM)) {
            commonService.throwException(DEFAULT_CATEGORY_CANNOT_UPDATE, locale, null, entity.getName());
        }

        Long currentUserId = commonService.getCurrentUserId();
        List<ExpenseCategory> existedCategories = categoryRepository.findDuplicateByName(request.getName(), entity.getSaveType(), entity.getId());
        //nếu cập nhật tên trùng với tên hiện tại của chính nó thì ko sao
        if (!existedCategories.isEmpty()) {
            ErrorCode errorCode = entity.getSaveType().equals(ACCOUNT) ? CATEGORY_NAME_EXISTED_BY_ACCOUNT : CATEGORY_NAME_EXISTED_BY_WALLET;
            commonService.throwException(errorCode, locale, null, request.getName());
        }

        entity = categoryMapper.toEntity(request, entity); //mapping ignore refId and saveType filed
        entity.setUpdatedAt(getNow());
        return responseFactory.success(categoryUpdateLog, categoryMapper.entityToResponse(categoryRepository.save(entity)));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> delete(Long id, Locale locale) {
        Long currentUserId = commonService.getCurrentUserId();
        Optional<ExpenseCategory> category = categoryRepository.findByIdAndOwner(id, currentUserId);
        if (category.isEmpty()) {
            commonService.throwException(CATEGORY_NOT_FOUND, locale, null);
        }

        ExpenseCategory entity = category.get();
        if (!entity.getType().equals(CUSTOM)) {
            commonService.throwException(DEFAULT_CATEGORY_CANNOT_UPDATE, locale, null, entity.getName());
        }

        entity.setStatus(DELETED);
        entity.setUpdatedAt(getNow());
        categoryRepository.save(entity);
        return responseFactory.success(categoryDeleteLog, true);
    }

    @Override
    public ResponseEntity<BaseResponse<Set<ExpenseCategoryResponse>>> getAll(Long walletId, Locale locale) {
        Set<ExpenseCategoryResponse> result = new HashSet<>();
//        if (saveType == null || !List.of(ACCOUNT, WALLET).contains(saveType)) {
//            saveType = null;
//            refId = null;
//        } else if (saveType.equals(ACCOUNT)) {
//            refId = commonService.getCurrentUserId();
//        }

        categoryRepository.findAllParentAccessByWallet(commonService.getCurrentUserId(), walletId)
                .forEach(parent -> {
                    ExpenseCategoryResponse response = categoryMapper.entityToResponse(parent);
                    response.setName(commonService.getMessageSrc(response.getName(), locale));
                    response.setDescription(commonService.getMessageSrc(response.getDescription(), locale));
                    fetchChildren(response, locale);
                    result.add(response);
                });

        return responseFactory.success(null, result);
    }

    @Override
    public ResponseEntity<BaseResponse<Set<String>>> getAllDefault(Locale locale) {
        //test với config YML file
        Set<String> listCategoryName = defaultCategoryConfiguration.getCategories().stream()
                .map(name -> commonService.getMessageSrc(name, locale))
                .collect(Collectors.toSet());
        return responseFactory.success(null, listCategoryName);
    }

    private void fetchChildren(ExpenseCategoryResponse response, Locale locale) {
        Set<ExpenseCategoryResponse> children = new HashSet<>();
        categoryRepository.findAllByParentId(response.getId())
                .forEach(child -> {
                    ExpenseCategoryResponse subResponse = categoryMapper.entityToResponse(child);
                    subResponse.setName(commonService.getMessageSrc(subResponse.getName(), locale));
                    subResponse.setDescription(commonService.getMessageSrc(subResponse.getDescription(), locale));
                    fetchChildren(subResponse, locale);
                    children.add(subResponse);
                });
        response.setChildren(children);
    }

    public ResponseEntity<BaseResponse<ExpenseCategory>> detail(Long id, Locale locale) {
        Optional<ExpenseCategory> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            commonService.throwException(CATEGORY_NOT_FOUND, locale, null, id);
        }
        return responseFactory.success(null, category.get());
    }
}

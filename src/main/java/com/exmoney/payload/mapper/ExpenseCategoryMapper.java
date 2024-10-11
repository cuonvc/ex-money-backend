package com.exmoney.payload.mapper;

import com.exmoney.entity.ExpenseCategory;
import com.exmoney.payload.request.expenseCategory.ExpenseCategoryRequest;
import com.exmoney.payload.response.expenseCategory.ExpenseCategoryResponse;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.lang.annotation.Target;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface ExpenseCategoryMapper {

    ExpenseCategory toEntity(ExpenseCategoryRequest request);

    @Mapping(target = "refId", ignore = true)
    @Mapping(target = "saveType", ignore = true)
    ExpenseCategory toEntity(ExpenseCategoryRequest request, @MappingTarget ExpenseCategory entity);

    ExpenseCategoryResponse entityToResponse(ExpenseCategory expenseCategory);
}

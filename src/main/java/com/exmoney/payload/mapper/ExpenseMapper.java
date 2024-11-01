package com.exmoney.payload.mapper;

import com.exmoney.entity.Expense;
import com.exmoney.payload.request.expense.ExpenseRequest;
import com.exmoney.payload.response.expense.ExpenseResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface ExpenseMapper {

    Expense toEntity(ExpenseRequest request);

    ExpenseResponse toResponse(Expense expense);
}

package com.exmoney.payload.mapper;

import com.exmoney.entity.Expense;
import com.exmoney.entity.ExpenseHistory;
import com.exmoney.payload.request.expense.ExpenseCreateRequest;
import com.exmoney.payload.response.expense.ExpenseResponse;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import static com.exmoney.payload.response.expense.ExpenseResponse.*;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface ExpenseMapper {

    @Mapping(target = PROP_ENTRY_DATE, ignore = true)
    Expense toEntity(ExpenseCreateRequest request);

    ExpenseResponse toResponse(Expense expense);

    @Mapping(target = "expenseId", source = "id")
    ExpenseHistory toHistory(Expense expense);
}

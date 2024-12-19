package com.exmoney.payload.request.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Data
public class ExpenseUpdateRequest {

    @Size(max = 100, message = "validate.expense_desc_length")
    private String description;

    @Range(min = 0, max = 100000000, message = "validate.expense_amount_size")
    @NotNull(message = "validate.expense_amount_empty")
    private BigDecimal amount;

    private String entryDate;

    @NotNull(message = "validate.expense_category_id_empty")
    private Long categoryId;
}

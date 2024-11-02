package com.exmoney.payload.request.expense;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Data
public class ExpenseRequest {

    @NotNull(message = "validate.expense_name_empty")
    @NotBlank(message = "validate.expense_name_empty")
    @NotEmpty(message = "validate.expense_name_empty")
    private String name;

    @Size(max = 100, message = "validate.expense_desc_length")
    private String description;

    @Range(min = 0, max = 100000000, message = "validate.expense_amount_size")
    @NotNull(message = "validate.expense_amount_empty")
    private BigDecimal amount;

//    private String currencyUnit; //VND, EUR, USD, GBP

    @NotNull(message = "validate.expense_type_empty")
    @NotBlank(message = "validate.expense_type_empty")
    @NotEmpty(message = "validate.expense_type_empty")
    private String type;

    @NotNull(message = "validate.expense_category_id_empty")
    @NotBlank(message = "validate.expense_category_id_empty")
    @NotEmpty(message = "validate.expense_category_id_empty")
    private String categoryId;

    @NotNull(message = "validate.expense_wallet_id_empty")
    @NotBlank(message = "validate.expense_wallet_id_empty")
    @NotEmpty(message = "validate.expense_wallet_id_empty")
    private String walletId;
}

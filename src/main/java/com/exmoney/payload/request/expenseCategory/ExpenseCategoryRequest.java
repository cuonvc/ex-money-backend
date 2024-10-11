package com.exmoney.payload.request.expenseCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCategoryRequest {
    private String iconImage; //provide by system

    private String color; //hex color code

    @NotNull(message = "validate.category_name_emty")
    @NotBlank(message = "validate.category_name_emty")
    @NotEmpty(message = "validate.category_name_emty")
    @Size(max = 30, message = "validate.category_name_size")
    private String name;

    private String description;

    private String parentId;

    @NotNull
    @NotBlank
    @NotEmpty
    private String saveType; //wallet or account

    private String refId; //walletId or userId - nullable: null -> userId
}

package com.exmoney.payload.request.scheduler;

import com.exmoney.payload.request.expense.ExpenseCreateRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExpenseSchedulerRequest {

    private ExpenseCreateRequest expense;

    @NotNull
    @NotEmpty
    @NotBlank
    private String timeInterval;

    @NotNull
    private Integer timeValue;
}

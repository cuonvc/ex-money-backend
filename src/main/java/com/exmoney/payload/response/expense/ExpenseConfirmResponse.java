package com.exmoney.payload.response.expense;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class ExpenseConfirmResponse {
    private String description;
    private BigDecimal amount;
    private Long walletId;
    private String walletName;
    private Long categoryId;
    private String categoryName;
}

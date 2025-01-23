package com.exmoney.payload.request.wallet;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletSettingRequest {

    private BigDecimal totalExpenseLimit;
    private BigDecimal expenseWarningLevel1;
    private BigDecimal expenseWarningLevel2;
    private BigDecimal expenseWarningLevel3;
}

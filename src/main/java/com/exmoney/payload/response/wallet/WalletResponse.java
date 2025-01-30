package com.exmoney.payload.response.wallet;

import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.payload.response.scheduler.SchedulerResponse;
import com.exmoney.payload.response.user.UserResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class WalletResponse {
    private Long id;
    private String status;
    private Long ownerUserId;
    private List<UserResponse> members;
    private String name;
    private String description;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private BigDecimal expenseLimit;
    private BigDecimal expenseWarningLevel1;
    private BigDecimal expenseWarningLevel2;
    private BigDecimal expenseWarningLevel3;
    private List<ExpenseResponse> expenses;
    private List<SchedulerResponse> schedulers;
    private List<Map<Long, String>> otherWallets;
    private Boolean isDefault;
    private String createdAt;
    private String updatedAt;
    private Long createdBy;
    private Long updatedBy;
}

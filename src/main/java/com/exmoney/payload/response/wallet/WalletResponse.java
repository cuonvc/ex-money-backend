package com.exmoney.payload.response.wallet;

import com.exmoney.payload.response.expense.ExpenseResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class WalletResponse {
    String id;
    String status;
    String ownerUserId;
    String name;
    String description;
    BigDecimal totalIncome;
    BigDecimal totalExpense;
    BigDecimal balance;
    List<ExpenseResponse> expenses;
    List<Map<String, String>> otherWallets;
    Boolean isDefault;
    String createdAt;
    String updatedAt;
}

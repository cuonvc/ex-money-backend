package com.exmoney.payload.response.expense;

import com.exmoney.payload.response.expenseCategory.ExpenseCategoryResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class ExpenseEditResource {
    private Long walletId;
    private String walletName;
    private List<Map<Long, String>> otherWalletMap;
    private Set<ExpenseCategoryResponse> categories;
}

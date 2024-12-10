package com.exmoney.payload.response.expense;

import com.exmoney.payload.response.expenseCategory.ExpenseCategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ExpenseFilterResource {

    private Long walletId;
    private String walletName;
    private Set<Map<Long, String>> otherWalletMap;
    private Set<Map<Long, String>> categories;
    private Set<Map<Long, String>> members;
}

package com.exmoney.payload.response.overview;

import com.exmoney.entity.Expense;
import com.exmoney.payload.response.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class HomeOverviewResponse {
    private UserResponse user;
    private BigDecimal totalExpenseAmount;
    private BigDecimal moreThanLastMonth;
    List<Expense> ownerExpenses;
}

package com.exmoney.payload.response.overview;

import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.payload.response.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class HomeOverviewResponse {
    private int currentMonth;
    private UserResponse user;
    private BigDecimal totalExpenseAmount;
    private BigDecimal moreThanLastMonth;
    private List<ExpenseResponse> ownerExpenses;
    private List<WeekMapAmount> weekMapAmount;
}

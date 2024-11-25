package com.exmoney.service.impl;

import com.exmoney.entity.Expense;
import com.exmoney.entity.User;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.mapper.UserMapper;
import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.payload.response.overview.HomeOverviewResponse;
import com.exmoney.payload.response.user.UserResponse;
import com.exmoney.repository.ExpenseRepository;
import com.exmoney.repository.UserRepository;
import com.exmoney.service.CommonService;
import com.exmoney.service.OverviewService;
import com.exmoney.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class OverviewServiceImpl implements OverviewService {

    private final CommonService commonService;
    private final ExpenseRepository expenseRepository;
    private final UserMapper userMapper;
    private final ResponseFactory responseFactory;

    @Override
    public ResponseEntity<BaseResponse<HomeOverviewResponse>> getHomeOverview(Integer month, Locale locale) {
        Long userId = commonService.getCurrentUserId();
        UserResponse userResponse = userMapper.entityToResponse(commonService.findUserByIdOrThrow(userId, locale, null));

        LocalDateTime localDateTime = LocalDateTime.now();
        if (month != null && month >= 1 && month <= 12) {
            localDateTime = localDateTime.withMonth(month);
        }

        log.info("LOCAL DATE TIME - {}", localDateTime);
        List<Expense> expenses = expenseRepository.findAllByOwner(userId, localDateTime.getYear(), localDateTime.getMonthValue());
        BigDecimal totalAmount = expenses.stream().map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return responseFactory.success(
                null,
                HomeOverviewResponse.builder()
                        .user(userResponse)
                        .totalExpenseAmount(totalAmount)
                        .moreThanLastMonth(BigDecimal.valueOf(300000)) //tạm
                        .ownerExpenses(expenses)
                        .build()
        );
    }
}

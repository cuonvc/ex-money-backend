package com.exmoney.service.impl;

import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.mapper.UserMapper;
import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.payload.response.overview.HomeOverviewResponse;
import com.exmoney.payload.response.user.UserResponse;
import com.exmoney.repository.ExpenseRepository;
import com.exmoney.service.CommonService;
import com.exmoney.service.OverviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

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
        List<ExpenseResponse> expenses = expenseRepository.findAllByOwner(userId, localDateTime.getYear(), localDateTime.getMonthValue())
                .stream().peek(e -> {
                    e.setCategoryName(commonService.getMessageSrc(e.getCategoryName(), locale));
                    e.setWalletName(commonService.getMessageSrc(e.getWalletName(), locale));
                })
                .toList();
        BigDecimal totalAmount = expenses.stream().map(ExpenseResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return responseFactory.success(
                null,
                HomeOverviewResponse.builder()
                        .currentMonth(localDateTime.getMonthValue())
                        .user(userResponse)
                        .totalExpenseAmount(totalAmount)
                        .moreThanLastMonth(BigDecimal.valueOf(300000)) //tạm
                        .ownerExpenses(expenses)
                        .dayMapAmount(dayMapAmount(localDateTime, userId))
                        .build()
        );
    }

    private Map<LocalDate, BigDecimal> dayMapAmount(LocalDateTime now, Long ownerId) {
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
        List<Object[]> rawData = expenseRepository.findEachDay(ownerId, startOfMonth, endOfMonth);

        return rawData.stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row[0]).toLocalDate(),
                        row -> (BigDecimal) row[1]
                ));
    }
}

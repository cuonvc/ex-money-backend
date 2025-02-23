package com.exmoney.service.impl;

import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.mapper.UserMapper;
import com.exmoney.payload.response.expense.ExpenseResponse;
import com.exmoney.payload.response.notification.NotificationResponse;
import com.exmoney.payload.response.overview.HomeOverviewResponse;
import com.exmoney.payload.response.overview.WeekMapAmount;
import com.exmoney.payload.response.user.UserResponse;
import com.exmoney.repository.ExpenseRepository;
import com.exmoney.repository.NotificationRepository;
import com.exmoney.service.CommonService;
import com.exmoney.service.OverviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.exmoney.util.Constant.DEFAULT_PAGE_OFFSET;
import static com.exmoney.util.Constant.DEFAULT_PAGE_SIZE;
import static com.exmoney.util.Utils.divideAmount;
import static com.exmoney.util.Utils.getExpenseTypeDisp;

@Service
@RequiredArgsConstructor
@Slf4j
public class OverviewServiceImpl implements OverviewService {

    private final CommonService commonService;
    private final ExpenseRepository expenseRepository;
    private final UserMapper userMapper;
    private final ResponseFactory responseFactory;
    private final NotificationRepository notificationRepository;

    @Override
    public ResponseEntity<BaseResponse<HomeOverviewResponse>> getHomeOverview(Integer month, Integer year, Locale locale) {
        Long userId = commonService.getCurrentUserId();
        UserResponse userResponse = userMapper.entityToResponse(commonService.findUserByIdOrThrow(userId, locale, null));

        LocalDateTime localDateTime = LocalDateTime.now();
        if (month != null && month >= 1 && month <= 12) {
            localDateTime = localDateTime.withMonth(month);
        }

        if (year != null) {
            localDateTime = localDateTime.withYear(year);
        }

        log.info("LOCAL DATE TIME - {}", localDateTime);
        List<ExpenseResponse> expenses = expenseRepository.findAllByOwner(userId, localDateTime.getYear(), localDateTime.getMonthValue(), DEFAULT_PAGE_OFFSET, DEFAULT_PAGE_SIZE)
                .stream().peek(e -> {
                    e.setCategoryName(commonService.getMessageSrc(e.getCategoryName(), locale));
                    e.setWalletName(commonService.getMessageSrc(e.getWalletName(), locale));
                    e.setType(commonService.getMessageSrc(getExpenseTypeDisp(e.getType()), locale));
                })
                .toList();
        BigDecimal totalAmount = expenses.stream().map(ExpenseResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Object compareWithPrevMonth = expenseRepository.compareWithPrevMonth(userId, localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth());

        Object[] array = (Object[]) compareWithPrevMonth;
        BigDecimal sumOfCurrentMonth = (BigDecimal) array[0];
        BigDecimal sumOfPrevMonth = (BigDecimal) array[1];
        List<NotificationResponse> notifications = notificationRepository.findAllByUser(userId,10, 0);
        return responseFactory.success(
                null,
                HomeOverviewResponse.builder()
                        .currentMonth(localDateTime.getMonthValue())
                        .user(userResponse)
                        .notifications(notifications)
                        .totalExpenseAmount(totalAmount)
                        .moreThanLastMonth(sumOfCurrentMonth.subtract(sumOfPrevMonth))
                        .ownerExpenses(expenses)
                        .weekMapAmount(weekMapAmount(expenses))
                        .build()
        );
    }

    private List<WeekMapAmount> weekMapAmount(List<ExpenseResponse> expenses) {
        BigDecimal amtWeek1 = BigDecimal.ZERO;
        BigDecimal amtWeek2 = BigDecimal.ZERO;
        BigDecimal amtWeek3 = BigDecimal.ZERO;
        BigDecimal amtWeek4 = BigDecimal.ZERO;
        BigDecimal amtWeek5 = BigDecimal.ZERO;

        for (ExpenseResponse expense : expenses) {
            LocalDateTime entryDate = expense.getEntryDate();
            int dayOfMonth = entryDate.getDayOfMonth();

            //expense list đã đảm bảo all entry date chỉ nằm trong tháng này
            if (dayOfMonth <= 7) {
                amtWeek1 = amtWeek1.add(expense.getAmount());
            } else if (dayOfMonth > 7 && dayOfMonth <= 14) {
                amtWeek2 = amtWeek2.add(expense.getAmount());
            } else if (dayOfMonth > 14 && dayOfMonth <= 21) {
                amtWeek3 = amtWeek3.add(expense.getAmount());
            } else if (dayOfMonth > 21 && dayOfMonth <= 28) {
                amtWeek4 = amtWeek4.add(expense.getAmount());
            } else {
                amtWeek5 = amtWeek5.add(expense.getAmount());
            }
        }

        return Arrays.asList(
          new WeekMapAmount(1, divideAmount(amtWeek1)),
          new WeekMapAmount(2, divideAmount(amtWeek2)),
          new WeekMapAmount(3, divideAmount(amtWeek3)),
          new WeekMapAmount(4, divideAmount(amtWeek4)),
          new WeekMapAmount(5, divideAmount(amtWeek5))
        );
    }
}

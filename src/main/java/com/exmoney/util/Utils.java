package com.exmoney.util;

import com.exmoney.exception.APIException;
import com.exmoney.exception.ServiceException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static com.exmoney.payload.enumerate.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.exmoney.util.Constant.ExpenseType.*;

public class Utils {

    public static LocalDateTime getNow() {
        return LocalDateTime.now();
    }

    public static String getNowStr() {
        return LocalDateTime.now().toString();
    }

    public static LocalDateTime clientToLocalDateTime(String dateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            throw new ServiceException("Invalid date time formatter", HttpStatus.BAD_REQUEST.name(), 400);
        }
    }

    public static BigDecimal divideAmount(BigDecimal amount) {
        return amount.divide(BigDecimal.valueOf(1000));
    }

    public static BigDecimal getMaxWithZero(BigDecimal amount) {
        return amount.max(BigDecimal.ZERO);
    }

    public static BigDecimal getMinWithHundred(BigDecimal amount) {
        return amount.min(BigDecimal.valueOf(100));
    }

    public static String getExpenseTypeDisp(String expenseType) {
        return switch (expenseType) {
            case MANUAL -> "display.expense_type.manual";
            case FROM_SCHEDULE -> "display.expense_type.from_schedule";
            case SCHEDULE -> "display.expense_type.schedule";
            default -> "";
        };
    }

    public static String generateOtpCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}

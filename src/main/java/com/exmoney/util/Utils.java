package com.exmoney.util;

import com.exmoney.exception.APIException;
import com.exmoney.exception.ServiceException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.exmoney.payload.enumerate.ErrorCode.INTERNAL_SERVER_ERROR;

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
}

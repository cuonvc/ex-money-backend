package com.exmoney.payload.response.overview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeekMapAmount {
//    private LocalDate date;
    private int week;
    private BigDecimal amount;

    public static final String PROP_DATE = "date";
    public static final String PROP_WEEK = "week";
    public static final String PROP_AMOUNT = "amount";

    public WeekMapAmount(HashMap<String, Object> response) {
//        this.date = ((LocalDateTime) response.get(PROP_DATE)).toLocalDate();
        this.week = (Integer) response.get(PROP_WEEK);
        this.amount = (BigDecimal) response.get(PROP_AMOUNT);
    }
}

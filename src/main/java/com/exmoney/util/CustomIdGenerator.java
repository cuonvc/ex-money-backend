package com.exmoney.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.exmoney.util.Utils.getNow;

public class CustomIdGenerator implements IdentifierGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        LocalDateTime now = getNow();
        return "" + now.getYear() + now.getMonthValue() + now.getDayOfMonth()
                + UUID.randomUUID().toString().replaceAll("-", "")
                + now.getHour() + now.getMinute() + now.getSecond();
    }
}

package com.exmoney.service;

import com.exmoney.entity.Notification;
import com.exmoney.entity.User;
import com.exmoney.payload.common.NotificationBuilder;
import com.exmoney.payload.enumerate.ErrorCode;
import com.exmoney.security.CustomUserDetail;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public interface CommonService {
    CustomUserDetail getCurrentUser();
    Long getCurrentUserId();
    String getMessageSrc(String messageCode, Locale locale);
    String getMessageSrcWithParam(String messageCode, Locale locale, Object... params);
    void throwException(ErrorCode errorCode, Locale locale, String log, Object... args);
    User findUserByIdOrThrow(Long id, Locale locale, String log);
    User findUserByEmailOrThrow(String email, Locale locale, String log);
    void actionLog(String log, String actionBy, int status);
    void actionLogAnonymous(String email);
    void pushNotification(NotificationBuilder builder);
    String idListToString(Set<Long> idList);
    Set<Long> stringToIdList(String idList);
}

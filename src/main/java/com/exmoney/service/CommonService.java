package com.exmoney.service;

import com.exmoney.entity.User;
import com.exmoney.payload.enumerate.ErrorCode;
import com.exmoney.security.CustomUserDetail;

import java.util.Locale;

public interface CommonService {
    CustomUserDetail getCurrentUser();
    String getCurrentUserId();
    String getMessageSrc(String messageCode, Locale locale);
    void throwException(ErrorCode errorCode, Locale locale, Object... args);
    User findUserByIdOrThrow(String id, Locale locale);
    User findUserByEmailOrThrow(String email, Locale locale);
}

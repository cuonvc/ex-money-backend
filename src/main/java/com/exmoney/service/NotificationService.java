package com.exmoney.service;

import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.NotificationBuilder;
import com.exmoney.payload.response.notification.NotificationResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Locale;

public interface NotificationService {
    void pushNotification(NotificationBuilder builder);

    ResponseEntity<BaseResponse<Boolean>> markRead(long id, Locale locale);
    ResponseEntity<BaseResponse<Boolean>> markReadAll(Locale locale);
    ResponseEntity<BaseResponse<List<NotificationResponse>>> getAll(int offset, int limit, Locale locale);
}

package com.exmoney.service;

import com.exmoney.payload.common.NotificationBuilder;
import org.springframework.stereotype.Service;

public interface NotificationService {
    void pushNotification(NotificationBuilder builder);
}

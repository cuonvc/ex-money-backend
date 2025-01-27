package com.exmoney.controller;

import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.response.notification.NotificationResponse;
import com.exmoney.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

import static com.exmoney.util.Constant.API_BASE_USER;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PutMapping(API_BASE_USER + "/notification/mark_read/{id}")
    public ResponseEntity<BaseResponse<Boolean>> markRead(@PathVariable Long id,
                                                                       @RequestParam Locale locale) {
        return notificationService.markRead(id, locale);
    }

    @PutMapping(API_BASE_USER + "/notification/mark_read/all")
    public ResponseEntity<BaseResponse<Boolean>> markReadAll(@RequestParam Locale locale) {
        return notificationService.markReadAll(locale);
    }
}

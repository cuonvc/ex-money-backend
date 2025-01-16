package com.exmoney.controller;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.exmoney.util.Constant.NotificationComponent.CONTENT;
import static com.exmoney.util.Constant.NotificationComponent.TITLE;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final FirebaseMessaging firebaseMessaging;

    @PostMapping("/fcm")
    public String pushNoti(@RequestParam String token) {
        Message message = Message.builder()
                .setToken(token)
                .putAllData(Map.of(TITLE, "Test", CONTENT, "Test push noti"))
                .setNotification(Notification.builder()
                        .setTitle("Test")
                        .setBody("Test push noti")
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setContentAvailable(true)
                                .setSound("default") // Thêm âm thanh nếu cần
                                .build())
                        .build())
                .build();
        String resp = null;
        try {
            resp = firebaseMessaging.sendAsync(message).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return resp;
    }
}

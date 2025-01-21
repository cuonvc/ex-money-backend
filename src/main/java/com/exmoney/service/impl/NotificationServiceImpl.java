package com.exmoney.service.impl;

import com.exmoney.entity.Notification;
import com.exmoney.entity.NotificationIdentity;
import com.exmoney.payload.common.NotificationBuilder;
import com.exmoney.repository.DeviceInfoRepository;
import com.exmoney.repository.NotificationIdentityRepository;
import com.exmoney.repository.NotificationRepository;
import com.exmoney.service.NotificationService;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.exmoney.util.Constant.NotificationComponent.*;
import static com.exmoney.util.Utils.getNow;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationIdentityRepository notificationIdentityRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final DeviceInfoRepository deviceInfoRepository;

    @Override
    @Transactional
    public void pushNotification(NotificationBuilder builder) {
        log.info("========> Trigger notification sending");
        LocalDateTime now = getNow();
        Map<String, String> fcmData = builder.getFcmData();
        Notification notification = notificationRepository.save(
                Notification.builder()
                        .title(fcmData.get(TITLE))
                        .content(fcmData.get(CONTENT))
                        .type(fcmData.get(TYPE))
                        .priority(builder.getPriority())
                        .createdAt(now)
                        .updatedAt(now)
                        .build());

        notificationIdentityRepository.saveAll(builder.getUserIdList().stream()
                .map(userId -> NotificationIdentity.builder()
                        .notificationId(notification.getId())
                        .userId(userId)
                        .seen(false)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()).collect(Collectors.toSet())
        );

        Set<String> deviceTokenList = deviceInfoRepository.findListDeviceToken(builder.getUserIdList());

        sendNotificationAsync(deviceTokenList, builder.getFcmData());
    }

    @Async
    protected void sendNotificationAsync(Set<String> deviceTokenList, Map<String, String> data) {
        if (!data.get(TITLE).isEmpty() && !data.get(CONTENT).isEmpty()) {
            log.info("LOGGGGG PUSH NOTI - {} - {} - {}", deviceTokenList.toString(), data);

            deviceTokenList.forEach(token -> {
                Message message = Message.builder()
                        .setToken(token)
                        .putAllData(data)
                        .setAndroidConfig(AndroidConfig.builder()
                                .setPriority(AndroidConfig.Priority.HIGH)
                                .build())
                        .setNotification(com.google.firebase.messaging.Notification.builder()
                                .setTitle(data.get(TITLE))
                                .setBody(data.get(CONTENT))
                                .build())
                        .setApnsConfig(ApnsConfig.builder()
                                .setAps(Aps.builder()
                                        .setContentAvailable(true)
                                        .build())
                                .build())
                        .build();
                String resp = null;
                try {
                    resp = firebaseMessaging.sendAsync(message).get();
                    log.info("==========> PUSH NOTI - data: {} - {}", data, resp);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            log.warn("==============> PUSH DATA NOT FOUND");
        }
    }
}

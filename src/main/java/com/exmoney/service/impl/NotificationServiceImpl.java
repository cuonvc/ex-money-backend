package com.exmoney.service.impl;

import com.exmoney.entity.Notification;
import com.exmoney.entity.NotificationIdentity;
import com.exmoney.payload.common.BaseResponse;
import com.exmoney.payload.common.NotificationBuilder;
import com.exmoney.payload.common.ResponseFactory;
import com.exmoney.payload.response.notification.NotificationResponse;
import com.exmoney.repository.DeviceInfoRepository;
import com.exmoney.repository.NotificationIdentityRepository;
import com.exmoney.repository.NotificationRepository;
import com.exmoney.service.CommonService;
import com.exmoney.service.NotificationService;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.exmoney.payload.enumerate.ErrorCode.NOTIFICATION_NOT_FOUND;
import static com.exmoney.util.Constant.NotificationComponent.*;
import static com.exmoney.util.Constant.Status.ACTIVE;
import static com.exmoney.util.Utils.getNow;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationIdentityRepository notificationIdentityRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final DeviceInfoRepository deviceInfoRepository;
    private final CommonService commonService;
    private final ResponseFactory responseFactory;

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
                        .status(ACTIVE)
                        .createdAt(now)
                        .updatedAt(now)
                        .build());

        notificationIdentityRepository.saveAll(builder.getUserIdList().stream()
                .map(userId -> NotificationIdentity.builder()
                        .notificationId(notification.getId())
                        .userId(userId)
                        .seen(false)
                        .status(ACTIVE)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()).collect(Collectors.toSet())
        );

        Set<String> deviceTokenList = deviceInfoRepository.findListDeviceToken(builder.getUserIdList());

        sendNotificationAsync(deviceTokenList, builder.getFcmData());
    }

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
                    resp = firebaseMessaging.send(message);
                    log.info("==========> PUSH NOTI - data: {} - {}", data, resp);
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace(); //không throw để continue tới token tiếp theo nếu token hiện tại lỗi
                }
            });
        } else {
            log.warn("==============> PUSH DATA NOT FOUND");
        }
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> markRead(long id, Locale locale) {
        Long currentUserId = commonService.getCurrentUserId();
        NotificationIdentity identity = notificationIdentityRepository.findToUpdate(id, currentUserId);
        if (identity == null) {
            commonService.throwException(NOTIFICATION_NOT_FOUND, locale, null);
        }

        LocalDateTime now = getNow();
        identity.setUpdatedAt(now);
        identity.setSeen(true);
        identity.setSeenAt(now);

        notificationIdentityRepository.save(identity);

        return responseFactory.success(null, true);
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<Boolean>> markReadAll(Locale locale) {
        Long currentUserId = commonService.getCurrentUserId();

        log.info("================ NOW - {}", getNow());

        notificationIdentityRepository.remarkAllByUser(currentUserId, getNow());
        return responseFactory.success(null, true);
    }

    @Override
    public ResponseEntity<BaseResponse<List<NotificationResponse>>> getAll(int offset, int limit, Locale locale) {
        Long userId = commonService.getCurrentUserId();
        return responseFactory.success(null, notificationRepository.findAllByUser(userId, offset, limit));
    }
}

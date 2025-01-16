package com.exmoney.service.impl;

import com.exmoney.entity.*;
import com.exmoney.entity.Notification;
import com.exmoney.exception.ServiceException;
import com.exmoney.payload.common.NotificationBuilder;
import com.exmoney.payload.enumerate.ErrorCode;
import com.exmoney.repository.*;
import com.exmoney.security.CustomUserDetail;
import com.exmoney.service.CommonService;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.exmoney.payload.enumerate.ErrorCode.USER_NOT_FOUND;
import static com.exmoney.util.Constant.ActionBy.ACTION_BY_USER;
import static com.exmoney.util.Constant.NotificationComponent.*;
import static com.exmoney.util.Constant.NotificationIdentityType.GROUP;
import static com.exmoney.util.Constant.NotificationIdentityType.USER;
import static com.exmoney.util.Utils.getNow;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonServiceImpl implements CommonService {

    private final MessageSource messageSource;
    private final UserRepository userRepository;
    private final ActionLogRepository actionLogRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationIdentityRepository notificationIdentityRepository;
    private final NotificationGroupRepository notificationGroupRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final DeviceInfoRepository deviceInfoRepository;

    @Override
    public CustomUserDetail getCurrentUser() {
        return (CustomUserDetail) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
    }

    @Override
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    @Override
    public String getMessageSrc(String messageCode, Locale locale) {
        try {
            return messageSource.getMessage(messageCode, null, locale);
        } catch (NoSuchMessageException e) {
            return messageCode;
        }
    }

    @Override
    public String getMessageSrcWithParam(String messageCode, Locale locale, Object... params) {
        try {
            return messageSource.getMessage(messageCode, params, locale);
        } catch (NoSuchMessageException e) {
            return messageCode;
        }
    }

    @Override
    public void throwException(ErrorCode errorCode, Locale locale, String log, Object... args) {
        if (log != null) {
            this.actionLog(log, ACTION_BY_USER, errorCode.getStatusCode());
        }
        throw new ServiceException(
                messageSource.getMessage(errorCode.getMessageCode(), null, locale),
                errorCode.getStatus(),
                errorCode.getStatusCode(),
                args
        );
    }

    @Override
    public User findUserByIdOrThrow(Long id, Locale locale, String log) {
        Optional<User> userOp = userRepository.findById(id);
        if (userOp.isEmpty()) {
            this.throwException(USER_NOT_FOUND, locale, log);
        }

        return userOp.get();
    }

    @Override
    public User findUserByEmailOrThrow(String email, Locale locale, String log) {
        Optional<User> userOp = userRepository.findByEmail(email);
        if (userOp.isEmpty()) {
            this.throwException(USER_NOT_FOUND, locale, email, log);
        }

        return userOp.get();
    }

    @Override
    public void actionLog(String log, String actionBy, int status) {
        //log action
        if (log != null) {
            actionLogRepository.save(
                    ActionLog.builder()
                            .log(log)
                            .status(status)
                            .actionBy(actionBy)
                            .createdAt(getNow())
                            .createdBy(actionBy.equals(ACTION_BY_USER) ? getCurrentUserId() : 0)
                            .build()
            );
        }
    }

    @Override
    public void actionLogAnonymous(String email) {
        //method use by login and register actions


    }

    @Override
    public String idListToString(Set<Long> idList) {
        return String.join(", ", idList.stream().map(String::valueOf).toList());
    }

    @Override
    public Set<Long> stringToIdList(String idList) {
        return Arrays.stream(idList.split(", ")).map(Long::valueOf).collect(Collectors.toSet());
    }

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

        NotificationIdentity identity = notificationIdentityRepository.save(
                NotificationIdentity.builder()
                        .notificationId(notification.getId())
                        .identityType(builder.getIdentifyType())
                        .identityId(builder.getIdentifier())
                        .seen(false)
                        .build());

        Set<String> deviceTokenList = new HashSet<>();
        if (builder.getIdentifyType().equals(USER)) {
            deviceTokenList = Collections.singleton(deviceInfoRepository.findDeviceTokenByUserId(builder.getIdentifier()));
        } else if (builder.getIdentifyType().equals(GROUP)){
            NotificationGroup group = notificationGroupRepository.getReferenceById(builder.getIdentifier());
            Set<Long> userIds = this.stringToIdList(group.getUserList());
            deviceTokenList = deviceInfoRepository.findListDeviceToken(userIds);
        }

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

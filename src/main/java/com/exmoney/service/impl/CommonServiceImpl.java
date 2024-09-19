package com.exmoney.service.impl;

import com.exmoney.entity.ActionLog;
import com.exmoney.entity.User;
import com.exmoney.exception.ServiceException;
import com.exmoney.payload.enumerate.ErrorCode;
import com.exmoney.repository.ActionLogRepository;
import com.exmoney.repository.UserRepository;
import com.exmoney.security.CustomUserDetail;
import com.exmoney.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

import static com.exmoney.payload.enumerate.ErrorCode.USER_NOT_FOUND;
import static com.exmoney.util.Constant.ActionBy.ACTION_BY_USER;
import static com.exmoney.util.Utils.getNow;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final MessageSource messageSource;
    private final UserRepository userRepository;
    private final ActionLogRepository actionLogRepository;

    @Override
    public CustomUserDetail getCurrentUser() {
        return (CustomUserDetail) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
    }

    @Override
    public String getCurrentUserId() {
        return getCurrentUser().getId();
    }

    @Override
    public String getMessageSrc(String messageCode, Locale locale) {
        return messageSource.getMessage(messageCode, null, locale);
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
    public User findUserByIdOrThrow(String id, Locale locale, String log) {
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
                            .createdBy(actionBy.equals(ACTION_BY_USER) ? getCurrentUserId() : "")
                            .build()
            );
        }
    }

    @Override
    public void actionLogAnonymous(String email) {
        //method use by login and register actions


    }
}

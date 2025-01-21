package com.exmoney.service.impl;

import com.exmoney.entity.*;
import com.exmoney.exception.ServiceException;
import com.exmoney.payload.enumerate.ErrorCode;
import com.exmoney.repository.*;
import com.exmoney.security.CustomUserDetail;
import com.exmoney.service.CommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.exmoney.payload.enumerate.ErrorCode.USER_NOT_FOUND;
import static com.exmoney.util.Constant.ActionBy.ACTION_BY_USER;
import static com.exmoney.util.Utils.getNow;

@Service
@RequiredArgsConstructor
@Slf4j
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
}

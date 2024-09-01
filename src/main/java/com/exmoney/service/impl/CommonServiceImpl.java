package com.exmoney.service.impl;

import com.exmoney.entity.User;
import com.exmoney.exception.ServiceException;
import com.exmoney.payload.enumerate.ErrorCode;
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

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final MessageSource messageSource;
    private final UserRepository userRepository;

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
    public void throwException(ErrorCode errorCode, Locale locale, Object... args) {
        throw new ServiceException(
                messageSource.getMessage(errorCode.getMessageCode(), null, locale),
                errorCode.getStatus(),
                errorCode.getStatusCode(),
                args
        );
    }

    @Override
    public User findUserByIdOrThrow(String id, Locale locale) {
        Optional<User> userOp = userRepository.findById(id);
        if (userOp.isEmpty()) {
            this.throwException(USER_NOT_FOUND, locale);
        }

        return userOp.get();
    }

    @Override
    public User findUserByEmailOrThrow(String email, Locale locale) {
        Optional<User> userOp = userRepository.findByEmail(email);
        if (userOp.isEmpty()) {
            this.throwException(USER_NOT_FOUND, locale, email);
        }

        return userOp.get();
    }
}

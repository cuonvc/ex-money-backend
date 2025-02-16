package com.exmoney.service.impl;

import com.exmoney.payload.enumerate.ErrorCode;
import com.exmoney.service.CommonService;
import com.exmoney.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final CommonService commonService;
    @Value("${exmoney.application.sender.email}")
    private String sendFrom;

    @Value("${exmoney.application.action_log.email_send_to_client}")
    private String sendToClient;

    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String content, Locale locale) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sendFrom);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            commonService.throwException(ErrorCode.ERROR_SEND_EMAIL, locale, sendToClient);
            log.error(e.getMessage());
        }
    }
}

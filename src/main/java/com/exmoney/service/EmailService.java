package com.exmoney.service;

import java.util.Locale;

public interface EmailService {
    void send(String to , String subject, String content, Locale locale);
}

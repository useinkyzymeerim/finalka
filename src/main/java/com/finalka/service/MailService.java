package com.finalka.service;

import jakarta.mail.MessagingException;

public interface MailService {
    void sendSimpleMessage(String to, String subject, String text);
}

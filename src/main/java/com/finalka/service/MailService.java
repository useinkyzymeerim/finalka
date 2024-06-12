package com.finalka.service;


public interface MailService {
    void sendSimpleMessage(String to, String subject, String text);
}

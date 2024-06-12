package com.finalka.service.impl;

import com.finalka.service.MailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender javaMailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
            message.setSubject(subject);
            helper.setTo(to);
            helper.setText(text, true);
            helper.setFrom(from);
            javaMailSender.send(message);
            log.info("Уведомление успешно отправлено на адрес {}", to);
        } catch (MessagingException e) {
            log.error("Ошибка при отправке уведомления на адрес {}", to, e);
            throw new RuntimeException("Ошибка при отправке уведомления", e);
        }
    }
}

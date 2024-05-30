package com.finalka.service;

import jakarta.mail.MessagingException;

public interface ReminderService {
    void setReminder(Long userId, int hour, int minute, String message);
    void cancelReminder(Long userId);
}

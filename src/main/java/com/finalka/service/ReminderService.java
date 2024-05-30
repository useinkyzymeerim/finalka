package com.finalka.service;

import jakarta.mail.MessagingException;

public interface ReminderService {
   void setReminder(Long userId, int hour, int minute, String message, String email);
    void cancelReminder(Long userId);
}

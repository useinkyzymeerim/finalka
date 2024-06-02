package com.finalka.service;

import com.finalka.dto.CreateReminderDto;
import com.finalka.dto.ReminderDto;

import java.util.List;

public interface ReminderService {
    void setReminder(int hour, int minute, String message);
    void cancelReminder(Long reminderId);
    List<ReminderDto> getAllReminders();
    void updateReminder(Long reminderId, CreateReminderDto reminderDto);
    void deleteReminder(Long reminderId);
}

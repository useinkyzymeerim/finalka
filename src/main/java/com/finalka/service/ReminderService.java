package com.finalka.service;

import com.finalka.dto.CreateReminderDto;
import com.finalka.dto.ReminderDto;

import java.util.List;

public interface ReminderService {
    Long setReminder(int hour, int minute, String message);
    void updateReminder(Long reminderId, CreateReminderDto reminderDto);
    void deleteReminder(Long reminderId);
    List<ReminderDto> getUserReminders();
    void cancelReminder(Long reminderId);

}

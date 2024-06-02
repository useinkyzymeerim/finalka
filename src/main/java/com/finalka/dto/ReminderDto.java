package com.finalka.dto;

import com.finalka.entity.Reminder;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReminderDto {
    private Long id;
    private Long userId;
    private LocalDateTime reminderTime;
    private String message;
    private String email;
    private Integer hour;
    private Integer minute;
    private String createdBy;
    private Timestamp createdAt;
    private String lastUpdatedBy;
    private Timestamp lastUpdatedAt;
    private String deletedBy;
    private Timestamp deletedAt;

    public static ReminderDto fromReminder(Reminder reminder) {
        ReminderDto dto = new ReminderDto();
        dto.setId(reminder.getId());
        dto.setUserId(reminder.getUserId());
        dto.setReminderTime(reminder.getReminderTime());
        dto.setMessage(reminder.getMessage());
        dto.setEmail(reminder.getEmail());
        dto.setHour(reminder.getHour());
        dto.setMinute(reminder.getMinute());
        dto.setCreatedBy(reminder.getCreatedBy());
        dto.setCreatedAt(reminder.getCreatedAt());
        dto.setLastUpdatedBy(reminder.getLastUpdatedBy());
        dto.setLastUpdatedAt(reminder.getLastUpdatedAt());
        dto.setDeletedBy(reminder.getDeletedBy());
        dto.setDeletedAt(reminder.getDeletedAt());
        return dto;
    }
}
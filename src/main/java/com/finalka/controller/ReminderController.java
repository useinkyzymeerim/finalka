package com.finalka.controller;

import com.finalka.service.MailService;
import com.finalka.service.impl.ReminderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderServiceImpl reminderService;

    @PostMapping("/set")
    @Transactional
    public ResponseEntity<String> setReminder(@RequestParam Long userId,
                                              @RequestParam int hour,
                                              @RequestParam int minute,
                                              @RequestParam String message) {
        try {
            reminderService.setReminder(userId, hour, minute, message);
            return ResponseEntity.ok("Напоминание установлено успешно.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось установить напоминание.");
        }
    }

    @DeleteMapping("/cancel/{userId}")
    @Transactional
    public ResponseEntity<String> cancelReminder(@PathVariable Long userId) {
        try {
            reminderService.cancelReminder(userId);
            return ResponseEntity.ok("Напоминание успешно отменено.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось отменить напоминание.");
        }
    }
}
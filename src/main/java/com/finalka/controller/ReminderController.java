package com.finalka.controller;

import com.finalka.service.impl.ReminderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reminders")
public class ReminderController {
    private final ReminderServiceImpl reminderService;

    @PostMapping("/set")
    public ResponseEntity<String> setReminder(@RequestParam Long userId,
                                              @RequestParam int hour,
                                              @RequestParam int minute,
                                              @RequestParam String message) {
        try {
            reminderService.setReminder(userId, hour, minute, message);
            return ResponseEntity.ok("Reminder set successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to set reminder.");
        }
    }

    @DeleteMapping("/cancel/{userId}")
    public ResponseEntity<String> cancelReminder(@PathVariable Long userId) {
        try {
            reminderService.cancelReminder(userId);
            return ResponseEntity.ok("Reminder canceled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to cancel reminder.");
        }
    }
}

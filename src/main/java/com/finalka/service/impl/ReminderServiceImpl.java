package com.finalka.service.impl;

import com.finalka.entity.Reminder;
import com.finalka.entity.User;
import com.finalka.repo.ReminderRepo;
import com.finalka.repo.UserRepo;
import com.finalka.service.MailService;
import com.finalka.service.ReminderService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.*;
@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final Map<Long, ScheduledFuture<?>> reminders = new ConcurrentHashMap<>();
    private final ReminderRepo reminderRepo;
    private final MailService mailService;
    private final UserRepo userRepo;

    @Transactional
    public void setReminder(Long userId, int hour, int minute, String message) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String email = user.getEmail();
        long delay = calculateDelay(hour, minute);

        LocalDateTime reminderTime = LocalDateTime.now().plusSeconds(delay / 1000);
        Reminder reminderEntity = new Reminder();
        reminderEntity.setUserId(userId);
        reminderEntity.setReminderTime(reminderTime);
        reminderEntity.setMessage(message);
        reminderEntity.setEmail(email);

        reminderRepo.save(reminderEntity);

        Runnable task = () -> {
            try {
                sendNotification(userId, message, email);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        };

        ScheduledFuture<?> future = executorService.schedule(task, delay, TimeUnit.MILLISECONDS);
        reminders.put(userId, future);
    }

    public void cancelReminder(Long userId) {
        ScheduledFuture<?> future = reminders.get(userId);
        if (future != null) {
            future.cancel(false);
            reminders.remove(userId);
        }
    }

    private void sendNotification(Long userId, String message, String email) throws MessagingException {
        mailService.sendSimpleMessage(email, "Уведомление", message);
        System.out.println("Уведомление успешно отправлено на адрес " + email);
    }

    private long calculateDelay(int hour, int minute) {
        LocalTime now = LocalTime.now();
        LocalTime reminderTime = LocalTime.of(hour, minute);
        if (reminderTime.isBefore(now)) {
            reminderTime = reminderTime.plusHours(24);
        }
        return Duration.between(now, reminderTime).toMillis();
    }
}
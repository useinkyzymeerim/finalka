package com.finalka.service.impl;

import com.finalka.dto.CreateReminderDto;
import com.finalka.dto.ReminderDto;
import com.finalka.entity.Reminder;
import com.finalka.entity.User;
import com.finalka.repo.ReminderRepo;
import com.finalka.repo.UserRepo;
import com.finalka.service.MailService;
import com.finalka.service.ReminderService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final Map<Long, ScheduledFuture<?>> reminders = new ConcurrentHashMap<>();
    private final ReminderRepo reminderRepo;
    private final MailService mailService;
    private final UserRepo userRepo;

    @Override
    @Transactional
    public Long setReminder(int hour, int minute, String message) {
        log.info("Установка напоминания на {}:{}", hour, minute);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.error("Проверка подлинности имеет значение null");
            throw new RuntimeException("Проверка подлинности имеет значение null");
        }

        String username = authentication.getName();
        log.info("Аутентифицированное имя пользователя: {}", username);

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден!"));

        String email = user.getEmail();
        LocalDateTime reminderTime = calculateNextReminderTime(hour, minute);

        Reminder reminderEntity = new Reminder();
        reminderEntity.setUserId(user.getId());
        reminderEntity.setReminderTime(reminderTime);
        reminderEntity.setMessage(message);
        reminderEntity.setEmail(email);
        reminderEntity.setHour(hour);
        reminderEntity.setMinute(minute);
        reminderEntity.setCreatedBy(username);
        reminderEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        Reminder savedReminder = reminderRepo.save(reminderEntity);
        log.info("Сохраненное напоминание: {}", savedReminder);

        scheduleReminderTask(savedReminder.getId(), message, email, hour, minute);

        return savedReminder.getId();
    }

    private LocalDateTime calculateNextReminderTime(int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);
        reminderTime = reminderTime.minusHours(6);
        if (now.compareTo(reminderTime) > 0) {
            reminderTime = reminderTime.plusDays(1);
        }
        return reminderTime;
    }

    private void scheduleReminderTask(Long reminderId, String message, String email, int hour, int minute) {
        LocalDateTime reminderTime = calculateNextReminderTime(hour, minute);
        long initialDelay = Duration.between(LocalDateTime.now(), reminderTime).toMillis();

        Runnable task = () -> {
            try {
                sendNotification(reminderId, message, email);
                scheduleNextReminder(reminderId, message, email, hour, minute);
            } catch (MessagingException e) {
                log.error("Ошибка при отправке уведомления", e);
                throw new RuntimeException(e);
            }
        };

        ScheduledFuture<?> future = executorService.schedule(task, initialDelay, TimeUnit.MILLISECONDS);
        reminders.put(reminderId, future);
        log.info("Запланированное первое напоминание с задержкой: {} ms", initialDelay);
    }

    private void scheduleNextReminder(Long reminderId, String message, String email, int hour, int minute) {
        LocalDateTime nextReminderTime = calculateNextReminderTime(hour, minute).plusDays(1);
        long delay = Duration.between(LocalDateTime.now(), nextReminderTime).toMillis();

        Runnable task = () -> {
            try {
                sendNotification(reminderId, message, email);
                scheduleNextReminder(reminderId, message, email, hour, minute);
            } catch (MessagingException e) {
                log.error("Ошибка при отправке уведомления", e);
                throw new RuntimeException(e);
            }
        };

        ScheduledFuture<?> future = executorService.schedule(task, delay, TimeUnit.MILLISECONDS);
        reminders.put(reminderId, future);
        log.info("Следующее напоминание по расписанию с задержкой: {} ms", delay);
    }

    @Override
    @Transactional
    public void cancelReminder(Long reminderId) {
        ScheduledFuture<?> future = reminders.get(reminderId);
        if (future != null) {
            future.cancel(false);
            reminders.remove(reminderId);
        } else {
            log.warn("Напоминание с идентификатором {} не найдено для отмены", reminderId);
            throw new EntityNotFoundException("Напоминание не найдено с идентификатором: " + reminderId);
        }
    }

    @Override
    @Transactional
    public void updateReminder(Long reminderId, CreateReminderDto reminderDto) {
        log.info("Обновление напоминания с идентификатором: {}", reminderId);
        Optional<Reminder> optionalReminder = reminderRepo.findByIdAndDeletedAtIsNull(reminderId);
        if (optionalReminder.isPresent()) {
            Reminder reminder = optionalReminder.get();

            if (reminder.getDeletedAt() != null) {
                log.warn("Напоминание с идентификатором {} помечено как удаленное", reminderId);
                throw new EntityNotFoundException("Напоминание не найдено с идентификатором: " + reminderId);
            }

            LocalDateTime reminderTime = calculateNextReminderTime(reminderDto.getHour(), reminderDto.getMinute());

            reminder.setReminderTime(reminderTime);
            reminder.setMessage(reminderDto.getMessage());
            reminder.setHour(reminderDto.getHour());
            reminder.setMinute(reminderDto.getMinute());
            reminder.setLastUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            reminder.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));

            reminderRepo.save(reminder);
            log.info("Напоминание обновлено: {}", reminder);

            ScheduledFuture<?> future = reminders.get(reminder.getId());
            if (future != null) {
                future.cancel(false);
                reminders.remove(reminder.getId());

                scheduleReminderTask(reminder.getId(), reminderDto.getMessage(), reminder.getEmail(), reminderDto.getHour(), reminderDto.getMinute());
            }
        } else {
            log.warn("Напоминание с идентификатором {} не найдено", reminderId);
            throw new EntityNotFoundException("Напоминание не найдено с идентификатором: " + reminderId);
        }
    }

    @Override
    @Transactional
    public void deleteReminder(Long reminderId) {
        log.info("Удаление напоминания с идентификатором: {}", reminderId);
        Optional<Reminder> optionalReminder = reminderRepo.findById(reminderId);
        if (optionalReminder.isPresent()) {
            Reminder reminder = optionalReminder.get();
            reminder.setDeletedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            reminder.setDeletedAt(new Timestamp(System.currentTimeMillis()));
            reminderRepo.save(reminder);
            log.info("Напоминание помечено как удаленное: {}", reminder);

            ScheduledFuture<?> future = reminders.get(reminderId);
            if (future != null) {
                future.cancel(false);
                reminders.remove(reminderId);
                log.info("Планируемая задача для напоминания с идентификатором {} была отменена.", reminderId);
            }
        } else {
            log.warn("Напоминание не найдено с идентификатором: {}", reminderId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReminderDto> getUserReminders() {
        log.info("СТАРТ: ReminderServiceImpl - getUserReminders()");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        if (currentUser == null) {
            log.error("Пользователь не найден по токену");
            throw new RuntimeException("Пользователь не найден по токену");
        }

        List<Reminder> reminders = reminderRepo.findByCreatedByAndDeletedAtIsNull(currentUser);
        List<ReminderDto> reminderDtos = reminders.stream()
                .map(reminder -> ReminderDto.builder()
                        .message(reminder.getMessage())
                        .email(reminder.getEmail())
                        .hour(reminder.getHour())
                        .minute(reminder.getMinute())
                        .build())
                .collect(Collectors.toList());

        log.info("КОНЕЦ: ReminderServiceImpl - getUserReminders()");
        return reminderDtos;
    }

    private void sendNotification(Long reminderId, String message, String email) throws MessagingException {
        log.info("Отправка уведомления на электронную почту: {}", email);
        mailService.sendSimpleMessage(email, "Напоминание", message);
        log.info("Уведомление успешно отправлено на {}", email);
    }
}
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
import java.time.LocalTime;
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

    @Transactional
    public void setReminder(int hour, int minute, String message) {
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
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);

        if (reminderTime.isBefore(now)) {
            reminderTime = reminderTime.plusDays(1);
        }

        long delay = Duration.between(now, reminderTime).toMillis();

        Reminder reminderEntity = new Reminder();
        reminderEntity.setUserId(user.getId());
        reminderEntity.setReminderTime(reminderTime);
        reminderEntity.setMessage(message);
        reminderEntity.setEmail(email);
        reminderEntity.setHour(hour);
        reminderEntity.setMinute(minute);
        reminderEntity.setCreatedBy(username);
        reminderEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        reminderRepo.save(reminderEntity);
        log.info("Сохраненное напоминание: {}", reminderEntity);

        Runnable task = () -> {
            try {
                sendNotification(user.getId(), message, email);
                scheduleNextReminder(user.getId(), message, email, hour, minute);
            } catch (MessagingException e) {
                log.error("Уведомление об ошибке отправки", e);
                throw new RuntimeException(e);
            }
        };

        ScheduledFuture<?> future = executorService.schedule(task, delay, TimeUnit.MILLISECONDS);
        reminders.put(reminderEntity.getId(), future);
        log.info("Запланированное первое напоминание с задержкой: {} ms", delay);
    }

    private void scheduleNextReminder(Long userId, String message, String email, int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextReminderTime = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0).plusDays(1);

        long delay = Duration.between(now, nextReminderTime).toMillis();

        Runnable task = () -> {
            try {
                sendNotification(userId, message, email);
                scheduleNextReminder(userId, message, email, hour, minute);
            } catch (MessagingException e) {
                log.error("Уведомление об ошибке отправки", e);
                throw new RuntimeException(e);
            }
        };

        ScheduledFuture<?> future = executorService.schedule(task, delay, TimeUnit.MILLISECONDS);
        reminders.put(userId, future);
        log.info("Следующее напоминание по расписанию с задержкой: {} ms", delay);
    }


    public void cancelReminder(Long userId) {
        ScheduledFuture<?> future = reminders.get(userId);
        if (future != null) {
            future.cancel(false);
            reminders.remove(userId);
        }
    }

    private void sendNotification(Long userId, String message, String email) throws MessagingException {
        log.info("Отправка уведомления на электронную почту: {}", email);
        mailService.sendSimpleMessage(email, "Уведомление", message);
        log.info("Уведомление успешно отправлено на {}", email);
    }

    @Transactional(readOnly = true)
    public List<ReminderDto> getAllReminders() {
        log.info("Получение всех активных напоминаний");
        List<ReminderDto> reminders = reminderRepo.findAllActive().stream()
                .map(ReminderDto::fromReminder)
                .collect(Collectors.toList());
        log.info("Извлечено {} активных напоминаний", reminders.size());
        return reminders;
    }

    private long calculateDelay(int hour, int minute) {
        log.debug("Расчет задержки для напоминания при {}:{}", hour, minute);
        LocalTime now = LocalTime.now();
        LocalTime reminderTime = LocalTime.of(hour, minute);
        if (reminderTime.isBefore(now)) {
            reminderTime = reminderTime.plusHours(24);
        }
        long delay = Duration.between(now, reminderTime).toMillis();
        log.debug("Расчетная задержка: {} ms", delay);
        return delay;
    }

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

            long delay = calculateDelay(reminderDto.getHour(), reminderDto.getMinute());
            LocalDateTime reminderTime = LocalDateTime.now().plusSeconds(delay / 1000);

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

                Runnable task = () -> {
                    try {
                        sendNotification(reminder.getUserId(), reminderDto.getMessage(), reminder.getEmail());
                    } catch (MessagingException e) {
                        log.error("Ошибка при отправке уведомления", e);
                        throw new RuntimeException(e);
                    }
                };

                ScheduledFuture<?> updatedFuture = executorService.schedule(task, delay, TimeUnit.MILLISECONDS);
                reminders.put(reminder.getId(), updatedFuture);
                log.info("Напоминание перепланировано с новым задержкой: {} мс", delay);
            }
        } else {
            log.warn("Напоминание с идентификатором {} не найдено", reminderId);
            throw new EntityNotFoundException("Напоминание не найдено с идентификатором: " + reminderId);
        }
    }

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

            // Cancel the scheduled task if exists
            ScheduledFuture<?> future = reminders.get(reminderId);
            if (future != null) {
                future.cancel(false);
                reminders.remove(reminderId);
                log.info("Scheduled task for reminder ID {} has been cancelled.", reminderId);
            }
        } else {
            log.warn("Напоминание не найдено с идентификатором: {}", reminderId);
        }
    }

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
                        .id(reminder.getId())
                        .userId(reminder.getUserId())
                        .reminderTime(reminder.getReminderTime())
                        .message(reminder.getMessage())
                        .email(reminder.getEmail())
                        .hour(reminder.getHour())
                        .minute(reminder.getMinute())
                        .createdBy(reminder.getCreatedBy())
                        .createdAt(reminder.getCreatedAt())
                        .lastUpdatedBy(reminder.getLastUpdatedBy())
                        .lastUpdatedAt(reminder.getLastUpdatedAt())
                        .deletedBy(reminder.getDeletedBy())
                        .deletedAt(reminder.getDeletedAt())
                        .build())
                .collect(Collectors.toList());

        log.info("КОНЕЦ: ReminderServiceImpl - getUserReminders()");
        return reminderDtos;
    }
}
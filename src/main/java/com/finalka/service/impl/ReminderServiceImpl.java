package com.finalka.service.impl;

import com.finalka.entity.Reminder;
import com.finalka.repo.ReminderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.concurrent.*;
@Service
@RequiredArgsConstructor
public class ReminderServiceImpl {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final Map<Long, ScheduledFuture<?>> reminders = new ConcurrentHashMap<>();
    private final ReminderRepo reminderRepo;

    // Метод для установки напоминания
    public void setReminder(Long userId, int hour, int minute, String message) {
        long delay = calculateDelay(hour, minute); // Рассчитываем задержку до времени напоминания

        LocalDateTime reminderTime = LocalDateTime.now().plusMinutes(delay);
        Reminder reminderEntity = new Reminder();
        reminderEntity.setUserId(userId);
        reminderEntity.setReminderTime(reminderTime);
        reminderEntity.setMessage(message);

        reminderRepo.save(reminderEntity);

        Runnable task = () -> sendNotification(userId, message);

        ScheduledFuture<?> future = executorService.schedule(task, delay, TimeUnit.MILLISECONDS);
        reminders.put(userId, future);
    }

    // Метод для отмены напоминания
    public void cancelReminder(Long userId) {
        ScheduledFuture<?> future = reminders.get(userId);
        if (future != null) {
            future.cancel(false); // Отменяем запланированную задачу
            reminders.remove(userId); // Удаляем информацию о задаче из Map
        }
    }

    // Метод для отправки уведомления пользователю по электронной почте
    private void sendNotification(Long userId, String message) {
        // Адрес электронной почты получателя
        String to = "адрес_получателя@example.com";

        // Адрес электронной почты отправителя
        String from = "ваш_адрес@example.com";

        // Пароль от электронной почты отправителя
        String password = "ваш_пароль";

        // Устанавливаем свойства для подключения к почтовому серверу
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.example.com"); // SMTP-сервер
        properties.put("mail.smtp.port", "587"); // Порт для отправки почты
        properties.put("mail.smtp.auth", "true"); // Авторизация требуется

        // Получаем объект сессии
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Создаем объект MimeMessage
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(from)); // Устанавливаем адрес отправителя
            emailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to)); // Устанавливаем адрес получателя
            emailMessage.setSubject("Уведомление"); // Устанавливаем тему письма
            emailMessage.setText(message); // Устанавливаем текст письма

            // Отправляем сообщение
            Transport.send(emailMessage);
            System.out.println("Уведомление успешно отправлено на адрес " + to);
        } catch (MessagingException e) {
            System.out.println("Ошибка отправки уведомления: " + e.getMessage());
        }
    }

    // Вспомогательный метод для рассчета задержки до времени напоминания
    private long calculateDelay(int hour, int minute) {
        LocalTime now = LocalTime.now();
        LocalTime reminderTime = LocalTime.of(hour, minute);
        if (reminderTime.isBefore(now)) {
            // Если время напоминания уже прошло на сегодня, добавляем 24 часа для установки напоминания на следующий день
            reminderTime = reminderTime.plusHours(24);
        }
        return TimeUnit.HOURS.toMillis(reminderTime.getHour() - now.getHour()) +
                TimeUnit.MINUTES.toMillis(reminderTime.getMinute() - now.getMinute());
    }

}

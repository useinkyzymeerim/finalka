package com.finalka.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reminder_seq_generator")
    @SequenceGenerator(name = "reminder_seq_generator", sequenceName = "reminder_seq", allocationSize = 1)
    private Long id;

    @NotNull(message = "Идентификатор пользователя не должен быть пустым")
    private Long userId;

    @NotNull(message = "Время напоминания не должно быть нулевым")
    @Future(message = "Время напоминания должно наступить в будущем")
    private LocalDateTime reminderTime;

    @NotBlank(message = "Сообщение не должно быть пустым")
    @Size(max = 255, message = "Длина сообщения не должна превышать 255 символов")
    private String message;
    private String email;
}


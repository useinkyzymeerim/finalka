package com.finalka.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
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
    private Long userId;
    private LocalDateTime reminderTime;

    @NotBlank(message = "это поле не должн быть пустым!")
    @Size(max = 255, message = "Длина сообщения не должна превышать 255 символов")
    private String message;
    private String email;

    @NotNull(message = "это поле не должн быть пустым!")
    @Min(value = 0, message = "Час должен быть в диапазоне от 0 до 23")
    @Max(value = 23, message = "Час должен быть в диапазоне от 0 до 23")
    private Integer hour;

    @NotNull(message = "это поле не должн быть пустым!")
    @Min(value = 0, message = "Минута должен быть в диапазоне от 0 до 59")
    @Max(value = 59, message = "Минута должен быть в диапазоне от 0 до 59")
    private Integer minute;

    private String createdBy;
    private Timestamp createdAt;

    private String lastUpdatedBy;
    private Timestamp lastUpdatedAt;

    private String deletedBy;
    private Timestamp deletedAt;
}


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

    @NotNull(message = "User ID must not be null")
    private Long userId;

    @NotNull(message = "Reminder time must not be null")
    @Future(message = "Reminder time must be in the future")
    private LocalDateTime reminderTime;

    @NotBlank(message = "Message must not be blank")
    @Size(max = 255, message = "Message must not exceed 255 characters")
    private String message;
    private String email;
}


package com.finalka.entity;

import jakarta.persistence.*;
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
    private Long userId;

    private LocalDateTime reminderTime;
    private String message;
}

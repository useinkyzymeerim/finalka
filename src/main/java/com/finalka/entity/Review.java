package com.finalka.entity;

import jakarta.validation.constraints.*;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    @NotNull(message = "Рецепт не должен быть пустым")
    private Recipes recipe;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Пользователь не должен быть пустым")
    private User user;

    @NotBlank(message = "Комментарий не должен быть пустым")
    @Size(max = 500, message = "Комментарий не должен превышать 500 символов")
    private String comment;

    @Min(value = 1, message = "Рейтинг должен быть не менее 1")
    @Max(value = 5, message = "Рейтинг должен быть не более 1 5")
    private int rating;

    @PastOrPresent(message = "Creation date must be in the past or present")
    private LocalDateTime createdAt;

    @PastOrPresent(message = "Update date must be in the past or present")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}



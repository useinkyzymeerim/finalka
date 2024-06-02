package com.finalka.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private Long id;

    @NotNull(message = "Идентификатор рецепта не должен быть пустым")
    private Long recipeId;

    @NotNull(message = "Идентификатор пользователя не должен быть пустым")
    private Long userId;

    @NotBlank(message = "Комментарий не должен быть пустым")
    @Size(max = 500, message = "Комментарий не должен превышать 500 символов")
    private String comment;

    @Min(value = 1, message = "Рейтинг должен быть не менее 1")
    @Max(value = 5, message = "Рейтинг должен быть не более 5")
    private int rating;

    @PastOrPresent(message = "Дата создания должна быть в прошлом или настоящем")
    private LocalDateTime createdAt;

    @PastOrPresent(message = "Дата обновления должна быть в прошлом или настоящем")
    private LocalDateTime updatedAt;
}




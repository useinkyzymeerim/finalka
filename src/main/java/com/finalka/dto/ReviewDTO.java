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

    @NotNull(message = "Recipe ID must not be null")
    private Long recipeId;

    @NotNull(message = "User ID must not be null")
    private Long userId;

    @NotBlank(message = "Comment must not be blank")
    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating;

    @PastOrPresent(message = "Creation date must be in the past or present")
    private LocalDateTime createdAt;

    @PastOrPresent(message = "Update date must be in the past or present")
    private LocalDateTime updatedAt;
}



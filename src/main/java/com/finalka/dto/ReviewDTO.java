package com.finalka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private Long id;
    private Long recipeId;
    private Long userId;
    private String comment;
    private int rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


package com.finalka.service;

import com.finalka.dto.ReviewDTO;

import java.util.List;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO reviewDTO);

    List<ReviewDTO> getReviewsByRecipeId(Long recipeId);
}

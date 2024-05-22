package com.finalka.service.impl;

import com.finalka.dto.ReviewDTO;
import com.finalka.entity.Recipes;
import com.finalka.entity.Review;
import com.finalka.entity.User;
import com.finalka.repo.RecipesRepo;
import com.finalka.repo.ReviewRepo;
import com.finalka.repo.UserRepo;
import com.finalka.service.ReviewService;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepo reviewRepository;

    @Autowired
    private RecipesRepo recipesRepository;

    @Autowired
    private UserRepo userRepository;

    @Override
    public ReviewDTO createReview(ReviewDTO reviewDTO) {

        Recipes recipe = recipesRepository.findById(reviewDTO.getRecipeId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + reviewDTO.getRecipeId()));

        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + reviewDTO.getUserId()));

        Review review = new Review();
        review.setRecipe(recipe);
        review.setUser(user);
        review.setComment(reviewDTO.getComment());
        review.setRating(reviewDTO.getRating());
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        return convertToReviewDTO(savedReview);
    }

    @Override
    public List<ReviewDTO> getReviewsByRecipeId(Long recipeId) {
        List<Review> reviews = reviewRepository.findByRecipeId(recipeId);


        return reviews.stream().map(this::convertToReviewDTO).collect(Collectors.toList());
    }

    private ReviewDTO convertToReviewDTO(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setId(review.getId());
        reviewDTO.setRecipeId(review.getRecipe().getId());
        reviewDTO.setUserId(review.getUser().getId());
        reviewDTO.setComment(review.getComment());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setCreatedAt(review.getCreatedAt());
        reviewDTO.setUpdatedAt(review.getUpdatedAt());
        return reviewDTO;
    }


}


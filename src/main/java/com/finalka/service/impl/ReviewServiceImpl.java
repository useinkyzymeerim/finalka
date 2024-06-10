package com.finalka.service.impl;

import com.finalka.dto.ReviewDTO;
import com.finalka.entity.Recipes;
import com.finalka.entity.Review;
import com.finalka.entity.User;
import com.finalka.exception.InvalidDataException;
import com.finalka.exception.ReviewNotFoundException;
import com.finalka.repo.RecipesRepo;
import com.finalka.repo.ReviewRepo;
import com.finalka.repo.UserRepo;
import com.finalka.service.ReviewService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepo reviewRepository;

    @Autowired
    private RecipesRepo recipesRepository;

    @Autowired
    private UserRepo userRepository;

    @Override
    public ReviewDTO createReview(ReviewDTO reviewDTO) throws ResourceNotFoundException, InvalidDataException {
        try {
            if (reviewDTO.getComment() == null || reviewDTO.getComment().isEmpty()) {
                throw new InvalidDataException("Комментарий не может быть пустым");
            }
            if (reviewDTO.getRating() == 0 || reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
                throw new InvalidDataException("Рейтинг должен быть между 1 и 5");
            }

            Recipes recipe = recipesRepository.findById(reviewDTO.getRecipeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Рецепт не найден с id: " + reviewDTO.getRecipeId()));

            User user = userRepository.findById(reviewDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + reviewDTO.getUserId()));

            Review review = new Review();
            review.setRecipe(recipe);
            review.setUser(user);
            review.setComment(reviewDTO.getComment());
            review.setRating(reviewDTO.getRating());
            review.setCreatedAt(LocalDateTime.now());
            review.setUpdatedAt(LocalDateTime.now());

            Review savedReview = reviewRepository.save(review);

            return convertToReviewDTO(savedReview);
        } catch (ResourceNotFoundException | InvalidDataException e) {
            log.error("Ошибка при создании отзыва: ", e);
            throw e;
        } catch (Exception e) {
            log.error("Внутренняя ошибка сервера: ", e);
            throw new InvalidDataException("Внутренняя ошибка сервера");
        }
    }


    @Transactional
    @Override
    public List<ReviewDTO> getReviewsByRecipeId(Long recipeId) {
        try {
            List<Review> reviews = reviewRepository.findByRecipeId(recipeId);

            if (reviews.isEmpty()) {
                return Collections.emptyList();
            }

            return reviews.stream()
                    .map(this::convertToReviewDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Ошибка при получении отзывов для рецепта с ID " + recipeId, e);
            throw new RuntimeException("Ошибка при получении отзывов для рецепта с ID " + recipeId, e);
        }
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


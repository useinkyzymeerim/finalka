package com.finalka.service.impl;


import com.finalka.dto.ProductDTO;
import com.finalka.dto.RecipeDetailsDTO;
import com.finalka.dto.RecipesDto;
import com.finalka.entity.Products;
import com.finalka.entity.Recipes;
import com.finalka.entity.RecipesWithProducts;
import com.finalka.repo.RecipesRepo;
import com.finalka.repo.RecipesWithProductsRepo;
import com.finalka.service.RecipesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeServiceImpl implements RecipesService {
    private final RecipesRepo recipesRepo;
    private final RecipesWithProductsRepo recipesWithProductsRepo;


    @Override
    public List<RecipeDetailsDTO> findRecipeDetails(Long recipeId) {
        return recipesRepo.findRecipeDetails(recipeId);
    }
    @Override
    public List<RecipesDto> findByProduct(String productName) {
        List<RecipesWithProducts> recipesWithProductsList = recipesWithProductsRepo.findByProduct_ProductName(productName);


        List<RecipesDto> recipesDTOList = recipesWithProductsList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return recipesDTOList;
    }
    private RecipesDto convertToDTO(RecipesWithProducts recipesWithProducts) {
        RecipesDto recipeDTO = new RecipesDto();
        recipeDTO.setId(recipesWithProducts.getRecipe().getId());
        recipeDTO.setNameOfFood(recipesWithProducts.getRecipe().getNameOfFood());

        return recipeDTO;
    }
    @Override
    public List<RecipesDto> findByRecipeNameIgnoreCase(String recipeName) {
        List<Recipes> recipes = recipesRepo.findByNameOfFoodIgnoreCase(recipeName);
        return recipes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    private RecipesDto convertToDTO(Recipes recipe) {
        RecipesDto recipeDTO = new RecipesDto();
        recipeDTO.setId(recipe.getId());
        recipeDTO.setNameOfFood(recipe.getNameOfFood());

        return recipeDTO;
    }

    @Override
    public RecipesDto save(RecipesDto recipesDto) {
        try {
            log.info("СТАРТ: RecipeServiceImpl - save() {}", recipesDto);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Recipes recipes = Recipes.builder()
                    .nameOfFood(recipesDto.getNameOfFood())
                    .createdBy(username)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            recipesDto.setId(recipesRepo.save(recipes).getId());
            recipesDto.setCreatedAt(recipes.getCreatedAt());
            recipesDto.setCreatedBy(username);

        } catch (Exception e) {
            log.error("Не удалось добавить рецепт в базу данных");
            throw new RuntimeException("Не удалось добавить рецепт в базу данных");
        }
        log.info("КОНЕЦ: RecipeServiceImpl - save {} ", recipesDto);
        return recipesDto;

    }
    @Override
    public String delete(Long id) {
        log.info("СТАРТ: RecipeServiceImpl - delete(). Удалить запись с id {}", id);
        Recipes recipes = recipesRepo.findByDeletedAtIsNullAndId(id);
        if (recipes == null) {
            log.error("Рецепт с id " + id + " не найдена!");
            throw new NullPointerException("Рецепт с id " + id + " не найдена!");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        recipes.setDeletedBy(username);
        recipes.setDeletedAt(new Timestamp(System.currentTimeMillis()));
        recipesRepo.save(recipes);
        log.info("КОНЕЦ: RecipeServiceImpl - delete(). Удаленна запись с id {}", id);
        return "Рецепт с id " + id + " была удалена!";
    }
    @Override
    public RecipesDto findById(Long id) {
        log.info("СТАРТ: RecipeServiceImpl - findById({})", id);

        Recipes recipes = recipesRepo.findByDeletedAtIsNullAndId(id);
        if (recipes == null) {
            log.error("Рецепт с id " + id + " не найдена!");
            throw new NullPointerException("Рецепт с id " + id + " не найдена!");
        }
        log.info("КОНЕЦ: RecipeServiceImpl - findById(). Recipe - {} ", recipes);
        return RecipesDto.builder()
                .Id(recipes.getId())
                .nameOfFood(recipes.getNameOfFood())
                .createdBy(recipes.getCreatedBy())
                .createdAt(recipes.getCreatedAt())
                .lastUpdatedBy(recipes.getLastUpdatedBy())
                .lastUpdatedAt(recipes.getLastUpdatedAt())
                .deletedBy(recipes.getDeletedBy())
                .deletedAt(recipes.getDeletedAt())
                .build();

    }
    @Override
    public List<RecipesDto> findAll() {
        log.info("СТАРТ: RecipeServiceImpl - findAll()");
        List<Recipes> recipesList = recipesRepo.findAllByDeletedAtIsNull();
        if (recipesList.isEmpty()) {
            log.error("Актуальных рецептов нет!");
            throw new NullPointerException("Актуальных рецептов нет!");
        }
        List<RecipesDto> recipesDtos = new ArrayList<>();
        for (Recipes recipes : recipesList) {
            RecipesDto recipesDto = RecipesDto.builder()
                    .Id(recipes.getId())
                    .nameOfFood(recipes.getNameOfFood())
                    .createdBy(recipes.getCreatedBy())
                    .createdAt(recipes.getCreatedAt())
                    .lastUpdatedBy(recipes.getLastUpdatedBy())
                    .lastUpdatedAt(recipes.getLastUpdatedAt())
                    .deletedBy(recipes.getDeletedBy())
                    .deletedAt(recipes.getDeletedAt())
                    .build();
            recipesDtos.add(recipesDto);
        }
        log.info("КОНЕЦ: RecipeServiceImpl - findAll()");
        return recipesDtos;
    }
    @Override
    public RecipesDto update(RecipesDto recipesDto) {
        log.info("СТАРТ: RecipeServiceImpl - update({})", recipesDto);
        Recipes recipes =recipesRepo.findByDeletedAtIsNullAndId(recipesDto.getId());
        if (recipes == null) {
            log.error("Рецепт с id " + recipesDto.getId() + " не найдена!");
            throw new NullPointerException("Рецепт с id " + recipesDto.getId() + " не найдена!");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();


        Recipes updatedRecipes = Recipes.builder()
                .id(recipesDto.getId())
                .nameOfFood(recipesDto.getNameOfFood())
                .createdBy(recipes.getCreatedBy())
                .createdAt(recipes.getCreatedAt())
                .lastUpdatedBy(username)
                .lastUpdatedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        recipesRepo.save(updatedRecipes);

        recipesDto.setCreatedBy(recipes.getCreatedBy());
        recipesDto.setCreatedAt(recipes.getCreatedAt());
        recipesDto.setLastUpdatedBy(username);
        recipesDto.setLastUpdatedAt(updatedRecipes.getLastUpdatedAt());

        log.info("КОНЕЦ: RecipeServiceImpl - update(). Обноленная запись - {}", recipesDto);
        return recipesDto;
    }




}


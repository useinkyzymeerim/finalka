package com.finalka.service.impl;


import com.finalka.dto.*;
import com.finalka.entity.*;
import com.finalka.enums.Units;
import com.finalka.repo.*;
import com.finalka.service.RecipesService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeServiceImpl implements RecipesService {
    private final RecipesRepo recipesRepo;
    private final RecipesWithProductsRepo recipesWithProductsRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;
    private final MenuRepo menuRepo;

    @Transactional
    @Override
    public RecipeDetailsDTO findRecipeDetails(Long recipeId) {
        Recipes recipe = recipesRepo.findByDeletedAtIsNullAndId(recipeId);
        if (recipe == null) {
            throw new RuntimeException("Рецепт не найден или удален");
        }

        Set<RecipesWithProducts> recipesWithProducts = recipe.getRecipesWithProducts();

        List<ProductDetailsDto> productDetailsDtos = recipesWithProducts.stream().map(rwp -> {
            return ProductDetailsDto.builder()
                    .productName(rwp.getProduct().getProductName())
                    .quantity(rwp.getQuantityOfProduct())
                    .unitsEnum(rwp.getUnitsEnum())
                    .build();
        }).collect(Collectors.toList());

        return RecipeDetailsDTO.builder()
                .recipeName(recipe.getNameOfFood())
                .productDetailsDtos(productDetailsDtos)
                .quantityOfProduct(recipe.getQuantityOfProduct())
                .build();
    }

    @Transactional
    @Override
    public List<RecipeWithProductDTO> findRecipesByProducts(List<String> userProducts) {
        List<String> lowerCaseProducts = userProducts.stream()
                .map(String::toLowerCase)
                .toList();

        List<Recipes> recipes = new ArrayList<>();
        for (String product : lowerCaseProducts) {
            recipes.addAll(recipesRepo.findByProductNameContainingIgnoreCase(product));
        }

        return mapToRecipeDTOList(recipes);
    }

    private List<RecipeWithProductDTO> mapToRecipeDTOList(List<Recipes> recipes) {
        return recipes.stream()
                .map(this::mapToRecipeDTO)
                .collect(Collectors.toList());
    }

    private RecipeWithProductDTO mapToRecipeDTO(Recipes recipe) {
        return RecipeWithProductDTO.builder()
                .id(recipe.getId())
                .nameOfFood(recipe.getNameOfFood())
                .description(recipe.getDescription())
                .imageBase64(recipe.getImageBase64())
                .linkOfVideo(recipe.getLinkOfVideo())
                .quantityOfProduct(recipe.getQuantityOfProduct())
                .cookingTime(recipe.getCookingTime())
                .products(mapToProductDTOList(recipe.getRecipesWithProducts()))
                .createdBy(recipe.getCreatedBy())
                .createdAt(recipe.getCreatedAt())
                .lastUpdatedBy(recipe.getLastUpdatedBy())
                .lastUpdatedAt(recipe.getLastUpdatedAt())
                .deletedBy(recipe.getDeletedBy())
                .deletedAt(recipe.getDeletedAt())
                .build();
    }

    private List<ProductDTO> mapToProductDTOList(Set<RecipesWithProducts> recipesWithProducts) {
        return recipesWithProducts.stream()
                .map(recipeProduct -> ProductDTO.builder()
                        .productName(recipeProduct.getProduct().getProductName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void createRecipeWithProducts(RecipeWithProductDTO recipeDTO) {
        try {
            log.info("START: RecipeServiceImpl - createRecipeWithProducts() {}", recipeDTO);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Recipes recipe = Recipes.builder()
                    .nameOfFood(recipeDTO.getNameOfFood())
                    .description(recipeDTO.getDescription())
                    .imageBase64(recipeDTO.getImageBase64())
                    .linkOfVideo(recipeDTO.getLinkOfVideo())
                    .quantityOfProduct(recipeDTO.getQuantityOfProduct())
                    .cookingTime(recipeDTO.getCookingTime())
                    .createdBy(username)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            if (recipeDTO.getUserId() != null) {
                User user = userRepo.findById(recipeDTO.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("Пользователь с Id " + recipeDTO.getUserId() + " не найден"));
                recipe.setUser(user);
            }

            Recipes savedRecipe = recipesRepo.save(recipe);
            Long recipeId = savedRecipe.getId();

            for (ProductDTO productDTO : recipeDTO.getProducts()) {
                List<Products> productList = productRepo.findByProductName(productDTO.getProductName());

                if (!productList.isEmpty()) {
                    for (Products product : productList) {
                        RecipesWithProducts recipeProduct = new RecipesWithProducts();
                        recipeProduct.setRecipe(recipe);
                        recipeProduct.setProduct(product);
                        recipeProduct.setQuantityOfProduct(productDTO.getQuantity());
                        recipeProduct.setUnitsEnum(productDTO.getUnitsEnum());
                        recipesWithProductsRepo.save(recipeProduct);
                    }
                } else {
                    Products product = Products.builder()
                            .productName(productDTO.getProductName())
                            .quantity(productDTO.getQuantity())
                            .unitsEnum(productDTO.getUnitsEnum())
                            .createdBy(username)
                            .createdAt(new Timestamp(System.currentTimeMillis()))
                            .build();
                    Products savedProduct = productRepo.save(product);
                    Long productId = savedProduct.getId();

                    addProductToRecipe(productId, recipeId, productDTO.getQuantity(), productDTO.getUnitsEnum());
                }
            }
            log.info("END: RecipeServiceImpl - createRecipeWithProducts {}", recipeDTO);
        } catch (Exception e) {
            log.error("Не удалось создать рецепт с продуктом", e);
            throw new RuntimeException("Не удалось создать рецепт с продуктом", e);
        }
    }

    private void addProductToRecipe(Long productId, Long recipeId, Integer quantity, Units unitsEnum) {
        try {
            Optional<Recipes> optionalRecipe = recipesRepo.findById(recipeId);
            if (optionalRecipe.isPresent()) {
                Recipes recipe = optionalRecipe.get();

                Optional<Products> optionalProduct = productRepo.findById(productId);
                if (optionalProduct.isPresent()) {
                    Products product = optionalProduct.get();

                    RecipesWithProducts recipeProduct = new RecipesWithProducts();
                    recipeProduct.setRecipe(recipe);
                    recipeProduct.setProduct(product);
                    recipeProduct.setQuantityOfProduct(quantity);
                    recipeProduct.setUnitsEnum(unitsEnum);
                    recipesWithProductsRepo.save(recipeProduct);

                } else {
                    throw new IllegalArgumentException("Продукт с id " + productId + " не найден");
                }
            } else {
                throw new IllegalArgumentException("Рецепт с id " + recipeId + " не найден");
            }
        } catch (Exception e) {
            log.error("Не удалость добавить продукт в рецепт", e);
            throw new RuntimeException("Не удалость добавить продукт в рецепт", e);
        }
    }

    @Transactional
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

    @Transactional
    @Override
    public List<RecipesDto> findAll() {
        log.info("СТАРТ: RecipeServiceImpl - findAll()");
        List<Recipes> recipesList = recipesRepo.findAllByDeletedAtIsNull();
        if (recipesList == null || recipesList.isEmpty()) {
            log.error("Список рецептов пуст");
            throw new NullPointerException("Список рецептов пуст");
        }
        List<RecipesDto> recipesDtoList = recipesList.stream()
                .map(r -> RecipesDto.builder()
                        .Id(r.getId())
                        .nameOfFood(r.getNameOfFood())
                        .createdBy(r.getCreatedBy())
                        .createdAt(r.getCreatedAt())
                        .lastUpdatedBy(r.getLastUpdatedBy())
                        .lastUpdatedAt(r.getLastUpdatedAt())
                        .deletedBy(r.getDeletedBy())
                        .deletedAt(r.getDeletedAt())
                        .build()
                )
                .collect(Collectors.toList());
        log.info("КОНЕЦ: RecipeServiceImpl - findAll()");
        return recipesDtoList;
    }

    @Transactional
    @Override
    public void addRecipeToMenu(Long menuId, Long recipeId) {
        log.info("START: RecipeServiceImpl - addRecipeToMenu(). Adding recipe {} to menu {}", recipeId, menuId);
        Menu menu = menuRepo.findById(menuId).orElseThrow(() -> new RuntimeException("Menu not found"));
        Recipes recipe = recipesRepo.findById(recipeId).orElseThrow(() -> new RuntimeException("Recipe not found"));

        menu.getRecipes().add(recipe);
        menuRepo.save(menu);

        log.info("END: RecipeServiceImpl - addRecipeToMenu(). Added recipe {} to menu {}", recipeId, menuId);
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

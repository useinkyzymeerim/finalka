package com.finalka.service.impl;


import com.finalka.dto.*;
import com.finalka.entity.*;
import com.finalka.enums.Units;
import com.finalka.filter.CustomAuthenticationFilter;
import com.finalka.repo.*;
import com.finalka.service.RecipesService;
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
    private final MenuRepo menuRepo;

    @Transactional
    @Override
    public RecipeDetailsDTO getRecipeWithProductsById(Long recipeId) {
        try {
            log.info("START: RecipeServiceImpl - getRecipeWithProductsById() {}", recipeId);

            Recipes recipe = recipesRepo.findById(recipeId)
                    .orElseThrow(() -> new RuntimeException("Рецепт с указанным ID не найден или удалён"));

            if (recipe.getDeletedAt() != null) {
                throw new RuntimeException("Рецепт с указанным ID был удалён");
            }

            List<RecipesWithProducts> recipeProducts = recipesWithProductsRepo.findByRecipe(recipe);

            List<ProductDetailsDto> productDetailsDtos = new ArrayList<>();
            for (RecipesWithProducts recipeProduct : recipeProducts) {
                ProductDetailsDto productDetailsDto = ProductDetailsDto.builder()
                        .productName(recipeProduct.getProduct().getProductName())
                        .quantity(recipeProduct.getQuantityOfProduct())
                        .unitsEnum(recipeProduct.getUnitsEnum())
                        .build();
                productDetailsDtos.add(productDetailsDto);
            }

            RecipeDetailsDTO recipeDetailsDTO = RecipeDetailsDTO.builder()
                    .recipeName(recipe.getNameOfFood())
                    .productDetailsDtos(productDetailsDtos)
                    .quantityOfProduct(recipe.getQuantityOfProduct())
                    .build();

            log.info("END: RecipeServiceImpl - getRecipeWithProductsById {}", recipeId);

            return recipeDetailsDTO;
        } catch (Exception e) {
            log.error("Не удалось получить рецепт с продуктами", e);
            throw new RuntimeException("Не удалось получить рецепт с продуктами", e);
        }
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
        Optional<Recipes> recipesOptional = recipesRepo.findByDeletedAtIsNullAndId(id);
        Recipes recipes = recipesOptional.orElseThrow(() -> {
            log.error("Рецепт с id " + id + " не найден!");
            return new NullPointerException("Рецепт с id " + id + " не найден!");
        });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (recipes == null) {
            log.error("Рецепт с id " + id + " не найден!");
            throw new NullPointerException("Рецепт с id " + id + " не найден!");
        }
        if (!recipes.getCreatedBy().equals(username)) {
            log.error("Пользователь {} не имеет прав на удаление рецепта с id {}", username, id);
            throw new SecurityException("Вы не имеете прав на удаление этого рецепта");
        }

        recipes.setDeletedBy(username);
        recipes.setDeletedAt(new Timestamp(System.currentTimeMillis()));
        recipesRepo.save(recipes);

        log.info("КОНЕЦ: RecipeServiceImpl - delete(). Удалена запись с id {}", id);
        return "Рецепт с id " + id + " был удален!";

    }

    @Override
    public RecipesDto findById(Long id) {
        log.info("СТАРТ: RecipeServiceImpl - findById({})", id);

        Optional<Recipes> recipesOptional = recipesRepo.findByDeletedAtIsNullAndId(id);
        Recipes recipes = recipesOptional.orElseThrow(() -> {
            log.error("Рецепт с id " + id + " не найден!");
            return new NullPointerException("Рецепт с id " + id + " не найден!");
        });

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

        List<RecipesDto> recipesDtos = new ArrayList<>();
        for (Recipes recipes : recipesList) {
            RecipesDto recipesDto = RecipesDto.builder()
                    .Id(recipes.getId())
                    .nameOfFood(recipes.getNameOfFood())
                    .imageBase64(recipes.getImageBase64())
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


    @Transactional
    @Override
    public List<RecipesDto> findAllByChef(String token) {
        log.info("СТАРТ: RecipeServiceImpl - findAllByChefFromToken()");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        if (currentUser == null) {
            log.error("Пользователь не найден по токену: {}", token);
            throw new RuntimeException("Пользователь не найден по токену");
        }

        List<Recipes> recipesList = recipesRepo.findByCreatedByAndDeletedAtIsNull(currentUser);
        List<RecipesDto> recipesDtos = new ArrayList<>();

        for (Recipes recipes : recipesList) {
            RecipesDto recipesDto = RecipesDto.builder()
                    .Id(recipes.getId())
                    .nameOfFood(recipes.getNameOfFood())
                    .imageBase64(recipes.getImageBase64())
                    .createdBy(recipes.getCreatedBy())
                    .createdAt(recipes.getCreatedAt())
                    .lastUpdatedBy(recipes.getLastUpdatedBy())
                    .lastUpdatedAt(recipes.getLastUpdatedAt())
                    .deletedBy(recipes.getDeletedBy())
                    .deletedAt(recipes.getDeletedAt())
                    .build();
            recipesDtos.add(recipesDto);
        }

        log.info("КОНЕЦ: RecipeServiceImpl - findAllByChefFromToken()");
        return recipesDtos;
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

    @Transactional
    @Override
    public RecipesDto update(RecipesDto recipesDto) {
        log.info("СТАРТ: RecipeServiceImpl - update({})", recipesDto);

        Optional<Recipes> recipesOptional = recipesRepo.findByDeletedAtIsNullAndId(recipesDto.getId());
        Recipes recipes = recipesOptional.orElseThrow(() -> {
            log.error("Рецепт с id " + recipesDto.getId() + " не найден!");
            return new NullPointerException("Рецепт с id " + recipesDto.getId() + " не найден!");
        });

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

        log.info("КОНЕЦ: RecipeServiceImpl - update(). Обновленная запись - {}", recipesDto);
        return recipesDto;
    }
}


package com.finalka.service.impl;


import com.finalka.dto.*;
import com.finalka.entity.*;
import com.finalka.enums.Units;
import com.finalka.exception.*;
import com.finalka.repo.*;
import com.finalka.service.RecipesService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
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
                    .orElseThrow(() -> new RecipeNotFoundException("Рецепт с указанным ID не найден или удалён"));

            if (recipe.getDeletedAt() != null) {
                throw new RecipeDeletedException("Рецепт с указанным ID был удалён");
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
        } catch (RecipeNotFoundException | RecipeDeletedException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Не удалось получить рецепт с продуктами", e);
            throw new RecipeServiceException("Не удалось получить рецепт с продуктами", e);
        }
    }


    @Transactional
    @Override
    public List<RecipeWithProductDTO> findRecipesByProducts(String products) {
        try {
            if (products == null || products.isEmpty()) {
                throw new IllegalArgumentException("Строка продуктов не должна быть пустой");
            }

            List<String> userProducts = Arrays.asList(products.split("\\s+"));

            if (userProducts.isEmpty()) {
                throw new IllegalArgumentException("Список продуктов не должен быть пустым");
            }

            List<String> lowerCaseProducts = userProducts.stream()
                    .map(String::toLowerCase)
                    .toList();

            List<Recipes> recipes = new ArrayList<>();
            for (String product : lowerCaseProducts) {
                recipes.addAll(recipesRepo.findByProductNameContainingIgnoreCaseAndDeletedAtIsNull(product));
            }

            return mapToRecipeDTOList(recipes);
        } catch (NullPointerException e) {
            log.error("Переданные продукты или результаты поиска содержат null", e);
            throw new RuntimeException("Произошла ошибка при обработке данных: null", e);
        } catch (IllegalArgumentException e) {
            log.error("Недопустимые аргументы: {}", products, e);
            throw new RuntimeException("Недопустимые аргументы: " + e.getMessage(), e);
        } catch (DataAccessException e) {
            log.error("Ошибка доступа к данным при поиске рецептов", e);
            throw new RuntimeException("Ошибка доступа к данным", e);
        } catch (Exception e) {
            log.error("Произошла непредвиденная ошибка при поиске рецептов", e);
            throw new RuntimeException("Произошла непредвиденная ошибка", e);
        }
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
                .portion(recipe.getPortion())
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

    @Transactional
    @Override
    public void createRecipeWithProducts(RecipeWithProductDTO recipeDTO) {
        try {
            log.info("START: RecipeServiceImpl - createRecipeWithProducts() {}", recipeDTO);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Recipes recipe = Recipes.builder()
                    .nameOfFood(recipeDTO.getNameOfFood())
                    .description(recipeDTO.getDescription())
                    .portion(recipeDTO.getPortion())
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
        log.info("СТАРТ: RecipeServiceImpl - delete(). Удаление записи с id {}", id);
        Optional<Recipes> recipesOptional = recipesRepo.findByDeletedAtIsNullAndId(id);
        Recipes recipes = recipesOptional.orElseThrow(() -> {
            log.error("Рецепт с id {} не найден!", id);
            return new RecipeNotFoundException("Рецепт с id " + id + " не найден!");
        });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (!recipes.getCreatedBy().equals(username)) {
            log.error("Пользователь {} не имеет прав на удаление рецепта с id {}", username, id);
            throw new SecurityException("Вы не имеете прав на удаление этого рецепта");
        }

        recipes.setDeletedBy(username);
        recipes.setDeletedAt(new Timestamp(System.currentTimeMillis()));
        recipesRepo.save(recipes);

        log.info("КОНЕЦ: RecipeServiceImpl - delete(). Запись с id {} удалена", id);
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
        try {
            List<Recipes> recipesList = recipesRepo.findAllByDeletedAtIsNull();
            if (recipesList.isEmpty()) {
                throw new RecipeNotFoundException("Рецепты не найдены");
            }

            List<RecipesDto> recipesDtos = recipesList.stream()
                    .map(recipe -> RecipesDto.builder()
                            .Id(recipe.getId())
                            .nameOfFood(recipe.getNameOfFood())
                            .imageBase64(recipe.getImageBase64())
                            .createdBy(recipe.getCreatedBy())
                            .createdAt(recipe.getCreatedAt())
                            .lastUpdatedBy(recipe.getLastUpdatedBy())
                            .lastUpdatedAt(recipe.getLastUpdatedAt())
                            .deletedBy(recipe.getDeletedBy())
                            .deletedAt(recipe.getDeletedAt())
                            .build())
                    .collect(Collectors.toList());

            log.info("КОНЕЦ: RecipeServiceImpl - findAll()");
            return recipesDtos;
        } catch (RecipeNotFoundException e) {
            log.error("RecipeNotFoundException: ", e);
            throw e;
        } catch (Exception e) {
            log.error("Exception: ", e);
            throw new RuntimeException("Ошибка при получении рецептов", e);
        }
    }



    @Transactional
    @Override
    public List<RecipesDto> findAllByChef(String token) {
        log.info("СТАРТ: RecipeServiceImpl - findAllByChefFromToken()");

        try {
            if (token == null || !token.startsWith("Bearer ")) {
                log.error("Неверный формат токена: {}", token);
                throw new IllegalArgumentException("Неверный формат токена");
            }

            String authToken = token.substring(7);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUser = authentication.getName();

            if (currentUser == null) {
                log.error("Пользователь не найден по токену: {}", authToken);
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
        } catch (IllegalArgumentException e) {
            log.error("Ошибка аргумента: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Произошла ошибка: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Произошла неожиданная ошибка: {}", e.getMessage());
            throw new RuntimeException("Произошла неожиданная ошибка", e);
        }
    }


    @Transactional
    @Override
    public String addRecipeToMenu(RecipeAddProductDto menuRecipeRequestDto) {
        try {
            Long menuId = menuRecipeRequestDto.getMenuId();
            Long recipeId = menuRecipeRequestDto.getRecipeId();

            log.info("START: RecipeServiceImpl - addRecipeToMenu(). Adding recipe {} to menu {}", recipeId, menuId);
            Menu menu = menuRepo.findById(menuId).orElseThrow(() -> new MenuNotFoundException("Меню не найдено"));
            Recipes recipe = recipesRepo.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException("Рецепт не найден"));

            menu.getRecipes().add(recipe);
            menuRepo.save(menu);

            log.info("END: RecipeServiceImpl - addRecipeToMenu(). Added recipe {} to menu {}", recipeId, menuId);

            return "Рецепт успешно добавлен в меню";
        } catch (MenuNotFoundException | RecipeNotFoundException e) {
            log.error("Ошибка при добавлении рецепта в меню", e);
            throw new RuntimeException(e.getMessage());
        } catch (DataAccessException e) {
            log.error("Ошибка доступа к базе данных", e);
            throw new RuntimeException("Ошибка доступа к базе данных", e);
        } catch (Exception e) {
            log.error("Произошла ошибка при добавлении рецепта в меню", e);
            throw new RuntimeException("Произошла ошибка при добавлении рецепта в меню", e);
        }
    }

    @Transactional
    public void updateRecipe(RecipeUpdateDTO recipeUpdateDTO) {
        try {
            log.info("START: RecipeServiceImpl - updateRecipe() {}", recipeUpdateDTO);

            Recipes recipe = recipesRepo.findById(recipeUpdateDTO.getId())
                    .orElseThrow(() -> new RecipeNotFoundException("Рецепт с указанным ID не найден"));

            if (recipe.getDeletedAt() != null) {
                throw new RecipeDeletedException("Рецепт с указанным ID был удалён");
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            recipe.setNameOfFood(recipeUpdateDTO.getNameOfFood());
            recipe.setDescription(recipeUpdateDTO.getDescription());
            recipe.setImageBase64(recipeUpdateDTO.getImageBase64());
            recipe.setLinkOfVideo(recipeUpdateDTO.getLinkOfVideo());
            recipe.setQuantityOfProduct(recipeUpdateDTO.getQuantityOfProduct());
            recipe.setCookingTime(recipeUpdateDTO.getCookingTime());
            recipe.setLastUpdatedBy(username);
            recipe.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));

            recipesRepo.save(recipe);

            for (ProductUpdateDTO productUpdateDTO : recipeUpdateDTO.getProducts()) {
                boolean isNewProduct = false;

                List<Products> productsList = productRepo.findByProductName(productUpdateDTO.getProductName());
                Products existingProduct = productsList.stream()
                        .filter(product -> product.getDeletedAt() == null)
                        .findFirst()
                        .orElse(null);

                if (existingProduct == null) {
                    Products newProduct = new Products();
                    newProduct.setProductName(productUpdateDTO.getProductName());
                    newProduct.setQuantity(productUpdateDTO.getQuantity());
                    newProduct.setUnitsEnum(productUpdateDTO.getUnitsEnum());
                    newProduct.setCreatedBy(username);
                    newProduct.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    productRepo.save(newProduct);

                    RecipesWithProducts newRecipesWithProducts = new RecipesWithProducts();
                    newRecipesWithProducts.setRecipe(recipe);
                    newRecipesWithProducts.setProduct(newProduct);
                    newRecipesWithProducts.setQuantityOfProduct(productUpdateDTO.getQuantity());
                    newRecipesWithProducts.setUnitsEnum(productUpdateDTO.getUnitsEnum());
                    recipesWithProductsRepo.save(newRecipesWithProducts);

                    isNewProduct = true;
                } else {
                    existingProduct.setQuantity(productUpdateDTO.getQuantity());
                    existingProduct.setUnitsEnum(productUpdateDTO.getUnitsEnum());
                    existingProduct.setLastUpdatedBy(username);
                    existingProduct.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    productRepo.save(existingProduct);

                    RecipesWithProducts recipesWithProducts = recipesWithProductsRepo.findByRecipeAndProduct(recipe, existingProduct)
                            .orElseThrow(() -> new ProductNotFoundException("Связь между рецептом и продуктом не найдена"));

                    recipesWithProducts.setQuantityOfProduct(productUpdateDTO.getQuantity());
                    recipesWithProducts.setUnitsEnum(productUpdateDTO.getUnitsEnum());
                    recipesWithProductsRepo.save(recipesWithProducts);
                }

                if (isNewProduct) {
                    RecipesWithProducts oldRecipesWithProducts = recipesWithProductsRepo.findByRecipeAndProduct(recipe, existingProduct)
                            .orElse(null);
                    if (oldRecipesWithProducts != null) {
                        recipesWithProductsRepo.delete(oldRecipesWithProducts);
                    }
                }
            }

            log.info("END: RecipeServiceImpl - updateRecipe() {}", recipeUpdateDTO);

        } catch (RecipeNotFoundException | RecipeDeletedException | ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Не удалось обновить рецепт с продуктами", e);
            throw new RuntimeException("Не удалось обновить рецепт с продуктами", e);
        }
    }

    @Transactional
    @Override
    public void removeProductFromRecipe(Long recipeId, Long productId) {
        try {
            log.info("START: RecipeServiceImpl - removeProductFromRecipe() recipeId: {}, productId: {}", recipeId, productId);

            Recipes recipe = recipesRepo.findById(recipeId)
                    .orElseThrow(() -> new RecipeNotFoundException("Рецепт с указанным ID не найден"));

            if (recipe.getDeletedAt() != null) {
                throw new RecipeDeletedException("Рецепт с указанным ID был удалён");
            }

            Products product = productRepo.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Продукт с указанным ID не найден"));

            if (product.getDeletedAt() != null) {
                throw new ProductDeletedException("Продукт с указанным ID был удалён");
            }

            RecipesWithProducts recipesWithProducts = recipesWithProductsRepo.findByRecipeAndProduct(recipe, product)
                    .orElseThrow(() -> new RecipeProductLinkNotFoundException("Связь между рецептом и продуктом не найдена"));

            recipesWithProductsRepo.delete(recipesWithProducts);

            log.info("END: RecipeServiceImpl - removeProductFromRecipe() recipeId: {}, productId: {}", recipeId, productId);

        } catch (RecipeNotFoundException | ProductNotFoundException | RecipeDeletedException | ProductDeletedException |
                 RecipeProductLinkNotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RecipeProductLinkRemovalException("Не удалось удалить связь продукта с рецептом", e);
        } catch (Exception e) {
            log.error("Не удалось удалить связь продукта с рецептом", e);
            throw new RecipeProductLinkRemovalException("Не удалось удалить связь продукта с рецептом", e);
        }
    }
}
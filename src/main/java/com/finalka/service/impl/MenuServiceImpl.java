package com.finalka.service.impl;


import com.finalka.dto.CreateMenuDto;
import com.finalka.dto.MenuDTO;
import com.finalka.dto.MenuWithRecipeDTO;
import com.finalka.dto.RecipesDto;
import com.finalka.entity.Menu;
import com.finalka.entity.Products;
import com.finalka.entity.Recipes;
import com.finalka.entity.RecipesWithProducts;
import com.finalka.enums.Units;
import com.finalka.repo.MenuRepo;
import com.finalka.repo.RecipesRepo;
import com.finalka.repo.RecipesWithProductsRepo;
import com.finalka.service.MenuService;
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
public class MenuServiceImpl implements MenuService {
    private final MenuRepo menuRepo;
    private final RecipesWithProductsRepo recipesWithProductsRepo;
    private final RecipesRepo recipesRepo;

    @Override
    public CreateMenuDto save(CreateMenuDto menuDTO) {
        try {
            log.info("START: MenuServiceImpl - save() {}", menuDTO);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Menu menu = Menu.builder()
                    .nameOfMenu(menuDTO.getNameOfMenu())
                    .createdBy(username)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            Menu savedMenu = menuRepo.save(menu);
            menuDTO.setId(savedMenu.getId());
            menuDTO.setCreatedAt(savedMenu.getCreatedAt());
            menuDTO.setCreatedBy(username);

            log.info("END: MenuServiceImpl - save {} ", menuDTO);
            return menuDTO;
        } catch (Exception e) {
            log.error("Failed to save menu", e);
            throw new RuntimeException("Failed to save menu", e);
        }
    }

    @Override
    public String delete(Long id) {
        log.info("СТАРТ: MenuServiceImpl - delete(). Удалить запись с id {}", id);
        Menu menu = menuRepo.findByDeletedAtIsNullAndId(id);
        if (menu == null) {
            log.error( "Меню с id " + id + " не найдена!");
            throw new NullPointerException("Меню с id " + id + " не найдена!");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        menu.setDeletedBy(username);
        menu.setDeletedAt(new Timestamp(System.currentTimeMillis()));
        menuRepo.save(menu);
        log.info("КОНЕЦ: MenuServiceImpl - delete(). Удаленна запись с id {}", id);
        return "Меню с id " + id + " была удалена!";
    }

    @Override
    public MenuDTO findById(Long id) {
        log.info("СТАРТ: MenuServiceImpl - findById({})", id);
        Menu menu = menuRepo.findByDeletedAtIsNullAndId(id);
        if (menu == null) {
            log.error("Меню с id " + id + " не найдена!");
            throw new NullPointerException("Меню с id " + id + " не найдена!");
        }
        log.info("КОНЕЦ: MenuServiceImpl - findById(). Menu - {} ", menu);
        return MenuDTO.builder()
                .id(menu.getId())
                .nameOfMenu(menu.getNameOfMenu())
                .createdBy(menu.getCreatedBy())
                .createdAt(menu.getCreatedAt())
                .lastUpdatedBy(menu.getLastUpdatedBy())
                .lastUpdatedAt(menu.getLastUpdatedAt())
                .deletedBy(menu.getDeletedBy())
                .deletedAt(menu.getDeletedAt())
                .build();

    }

    @Override
    public List<MenuDTO> findAll() {
        log.info("СТАРТ: MenuServiceImpl - findAll()");
        List<Menu> menuList = menuRepo.findAllByDeletedAtIsNull();
        if (menuList.isEmpty()) {
            log.error("Актуальных меню нет!");
            throw new NullPointerException("Актуальных меню нет!");
        }
        List<MenuDTO> menuDTOS = new ArrayList<>();
        for (Menu menu : menuList) {
            MenuDTO menuDTO = MenuDTO.builder()
                    .id(menu.getId())
                    .nameOfMenu(menu.getNameOfMenu())
                    .createdBy(menu.getCreatedBy())
                    .createdAt(menu.getCreatedAt())
                    .lastUpdatedBy(menu.getLastUpdatedBy())
                    .lastUpdatedAt(menu.getLastUpdatedAt())
                    .deletedBy(menu.getDeletedBy())
                    .deletedAt(menu.getDeletedAt())
                    .build();
            menuDTOS.add(menuDTO);
        }
        log.info("КОНЕЦ: MenuServiceImpl - findAll()");
        return menuDTOS;
    }

    @Transactional
    @Override
    public Map<Products, Map.Entry<Integer, Units>> calculateRequiredProductsForMenu(Long id) {
        List<RecipesWithProducts> recipesWithProductsList = recipesWithProductsRepo.findByRecipe_Menu_Id(id);

        Map<Products, Map.Entry<Integer, Units>> productQuantityMap = new HashMap<>();

        for (RecipesWithProducts recipesWithProducts : recipesWithProductsList) {
            Products product = recipesWithProducts.getProduct();
            int quantity = recipesWithProducts.getQuantityOfProduct();
            Units unit = product.getUnitsEnum();

            // Проверяем, если продукт уже есть в карте, добавляем количество
            if (productQuantityMap.containsKey(product)) {
                Map.Entry<Integer, Units> entry = productQuantityMap.get(product);
                int totalQuantity = entry.getKey() + quantity;
                productQuantityMap.put(product, new AbstractMap.SimpleEntry<>(totalQuantity, unit));
            } else {
                productQuantityMap.put(product, new AbstractMap.SimpleEntry<>(quantity, unit));
            }
        }

        return productQuantityMap;
    }

    @Override
    public List<MenuWithRecipeDTO> getMenuWithRecipes(Long menuId) {
        List<MenuWithRecipeDTO> menuWithRecipesDTOList = new ArrayList<>();

        Optional<Menu> menuOptional = menuRepo.findById(menuId);
        menuOptional.ifPresent(menu -> {
            MenuWithRecipeDTO menuWithRecipesDTO = new MenuWithRecipeDTO();
            menuWithRecipesDTO.setMenuId(menu.getId());
            menuWithRecipesDTO.setNameOfMenu(menu.getNameOfMenu());

            List<RecipesDto> recipeDTOList = new ArrayList<>();
            for (Recipes recipe : menu.getRecipes()) {
                RecipesDto recipeDTO = new RecipesDto();
                recipeDTO.setId(recipe.getId());
                recipeDTO.setNameOfFood(recipe.getNameOfFood());
                recipeDTOList.add(recipeDTO);
            }
            menuWithRecipesDTO.setRecipes(recipeDTOList);
            menuWithRecipesDTOList.add(menuWithRecipesDTO);
        });

        return menuWithRecipesDTOList;
    }

    @Transactional
    public List<RecipesDto> getRecipesByMenuId(Long menuId) {
        Menu menu = menuRepo.findByDeletedAtIsNullAndId(menuId);
        if (menu == null) {
            throw new RuntimeException("Меню не найдено или удалено");
        }
        return menu.getRecipes().stream()
                .map(recipe -> RecipesDto.builder()
                        .nameOfFood(recipe.getNameOfFood())
                        .imageBase64(recipe.getImageBase64())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public MenuDTO update(MenuDTO menuDTO) {
        log.info("СТАРТ: MenuServiceImpl - update({})", menuDTO);
        Menu menu = menuRepo.findByDeletedAtIsNullAndId(menuDTO.getId());
        if (menu == null) {
            log.error("Меню с id " + menuDTO.getId() + " не найдена!");
            throw new NullPointerException("Меню с id " + menuDTO.getId() + " не найдена!");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();


        Menu updatedMenu = Menu.builder()
                .id(menuDTO.getId())
                .nameOfMenu(menuDTO.getNameOfMenu())
                .createdBy(menu.getCreatedBy())
                .createdAt(menu.getCreatedAt())
                .lastUpdatedBy(username)
                .lastUpdatedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        menuRepo.save(updatedMenu);

        menuDTO.setCreatedBy(menu.getCreatedBy());
        menuDTO.setCreatedAt(menu.getCreatedAt());
        menuDTO.setLastUpdatedBy(username);
        menuDTO.setLastUpdatedAt(updatedMenu.getLastUpdatedAt());

        log.info("КОНЕЦ: MenuServiceImpl - update(). Обноленная запись - {}", menuDTO);
        return menuDTO;
    }
}
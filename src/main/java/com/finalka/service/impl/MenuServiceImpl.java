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
import com.finalka.exception.MenuNotFoundException;
import com.finalka.exception.MenuSaveException;
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

    @Override
    public CreateMenuDto save(CreateMenuDto menuDTO) {
        try {
            log.info("START: MenuServiceImpl - save() {}", menuDTO);

            validateMenuDto(menuDTO);

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
        } catch (RuntimeException e) {
            log.error("Ошибка при сохранении меню: {}", menuDTO, e);
            throw new MenuSaveException("Ошибка при сохранении меню", e);
        } catch (Exception e) {
            log.error("Произошла неизвестная ошибка при сохранении меню: {}", menuDTO, e);
            throw new MenuSaveException("Произошла неизвестная ошибка при сохранении меню", e);
        }
    }

    private void validateMenuDto(CreateMenuDto menuDTO) {
        if (menuDTO.getNameOfMenu() == null || menuDTO.getNameOfMenu().isEmpty()) {
            throw new MenuSaveException("Название меню не должно быть пустым");
        }

    }


    @Override
    public String delete(Long id) {
        try {
            log.info("СТАРТ: MenuServiceImpl - delete(). Удалить запись с id {}", id);

            Optional<Menu> menuOptional = menuRepo.findByDeletedAtIsNullAndId(id);
            if (menuOptional.isEmpty()) {
                log.error("Меню с id {} не найдено!", id);
                throw new MenuNotFoundException("Меню с id " + id + " не найдено!");
            }

            Menu menu = menuOptional.get();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            menu.setDeletedBy(username);
            menu.setDeletedAt(new Timestamp(System.currentTimeMillis()));
            menuRepo.save(menu);

            log.info("КОНЕЦ: MenuServiceImpl - delete(). Удалена запись с id {}", id);
            return "Меню с id " + id + " было удалено!";
        } catch (MenuNotFoundException e) {
            log.error("Ошибка при удалении меню с id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Произошла неизвестная ошибка при удалении меню с id {}: {}", id, e);
            throw new RuntimeException("Произошла неизвестная ошибка при удалении меню", e);
        }
    }


    @Override
    public MenuDTO findById(Long id) {
        try {
            log.info("СТАРТ: MenuServiceImpl - findById({})", id);
            Optional<Menu> menuOptional = menuRepo.findByDeletedAtIsNullAndId(id);
            if (menuOptional.isEmpty()) {
                log.error("Меню с id {} не найдено!", id);
                throw new MenuNotFoundException("Меню с id " + id + " не найдено!");
            }

            Menu menu = menuOptional.get();
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
        } catch (MenuNotFoundException e) {
            log.error("Ошибка при поиске меню с id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Произошла неизвестная ошибка при поиске меню с id {}: {}", id, e);
            throw new RuntimeException("Произошла неизвестная ошибка при поиске меню", e);
        }
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
    public List<Map<String, Object>> calculateRequiredProductsForMenu(Long id) {
        try {
            List<RecipesWithProducts> recipesWithProductsList = recipesWithProductsRepo.findByRecipe_Menus_Id(id);

            Map<Products, Map.Entry<Integer, Units>> productQuantityMap = new HashMap<>();

            for (RecipesWithProducts recipesWithProducts : recipesWithProductsList) {
                Products product = recipesWithProducts.getProduct();
                int quantity = recipesWithProducts.getQuantityOfProduct();
                Units unit = product.getUnitsEnum();

                if (productQuantityMap.containsKey(product)) {
                    Map.Entry<Integer, Units> entry = productQuantityMap.get(product);
                    int totalQuantity = entry.getKey() + quantity;
                    productQuantityMap.put(product, new AbstractMap.SimpleEntry<>(totalQuantity, unit));
                } else {
                    productQuantityMap.put(product, new AbstractMap.SimpleEntry<>(quantity, unit));
                }
            }

            List<Map<String, Object>> productQuantityList = new ArrayList<>();
            for (Map.Entry<Products, Map.Entry<Integer, Units>> entry : productQuantityMap.entrySet()) {
                Map<String, Object> productInfo = new HashMap<>();
                productInfo.put("productName", entry.getKey().getProductName());
                productInfo.put("quantity", entry.getValue().getKey());
                productInfo.put("unit", entry.getValue().getValue().toString());
                productQuantityList.add(productInfo);
            }

            return productQuantityList;

        } catch (Exception e) {
            log.error("Не удалось рассчитать необходимые продукты для меню с идентификатором: {}", id, e);
            throw new RuntimeException("Не удалось рассчитать продукты ", e);
        }
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
        try {
            Optional<Menu> menuOptional = menuRepo.findByDeletedAtIsNullAndId(menuId);
            if (menuOptional.isEmpty()) {
                throw new MenuNotFoundException("Меню не найдено или удалено");
            }
            Menu menu = menuOptional.get();
            return menu.getRecipes().stream()
                    .map(recipe -> RecipesDto.builder()
                            .nameOfFood(recipe.getNameOfFood())
                            .imageBase64(recipe.getImageBase64())
                            .build())
                    .collect(Collectors.toList());
        } catch (MenuNotFoundException e) {
            log.error("Меню не найдено или удалено: {}", menuId, e);
            throw e;
        } catch (Exception e) {
            log.error("Произошла неизвестная ошибка при получении рецептов по ID меню: {}", menuId, e);
            throw new RuntimeException("Произошла неизвестная ошибка при получении рецептов", e);
        }
    }


    @Override
    public MenuDTO update(MenuDTO menuDTO) {
        try {
            log.info("СТАРТ: MenuServiceImpl - update({})", menuDTO);
            Optional<Menu> menuOptional = menuRepo.findByDeletedAtIsNullAndId(menuDTO.getId());
            if (menuOptional.isEmpty()) {
                log.error("Меню с id {} не найдено!", menuDTO.getId());
                throw new MenuNotFoundException("Меню с id " + menuDTO.getId() + " не найдено!");
            }
            Menu menu = menuOptional.get();

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

            log.info("КОНЕЦ: MenuServiceImpl - update(). Обновленная запись - {}", menuDTO);
            return menuDTO;
        } catch (MenuNotFoundException e) {
            log.error("Ошибка при обновлении меню с id {}: {}", menuDTO.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Произошла неизвестная ошибка при обновлении меню с id {}: {}", menuDTO.getId(), e);
            throw new RuntimeException("Произошла неизвестная ошибка при обновлении меню", e);
        }
    }
}
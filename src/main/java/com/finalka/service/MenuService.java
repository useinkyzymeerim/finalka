package com.finalka.service;


import com.finalka.dto.MenuDTO;
import com.finalka.dto.MenuWithRecipeDTO;
import com.finalka.entity.Products;

import java.util.List;
import java.util.Map;

public interface MenuService {

    List<MenuDTO> findAll() throws Exception;

    List<MenuWithRecipeDTO> getMenuWithRecipes(Long menuId);

    MenuDTO findById(Long id);
    Map<Products, Integer> calculateRequiredProductsForMenu (Long id);
    MenuDTO save(MenuDTO menuDTO);
    MenuDTO update(MenuDTO menuDTO);
    String delete(Long id);


}

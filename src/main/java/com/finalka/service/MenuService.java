package com.finalka.service;


import com.finalka.dto.CreateMenuDto;
import com.finalka.dto.MenuDTO;
import com.finalka.dto.MenuWithRecipeDTO;
import com.finalka.dto.RecipesDto;
import com.finalka.entity.Products;
import com.finalka.enums.Units;

import java.util.List;
import java.util.Map;

public interface MenuService {

    List<MenuDTO> findAll() throws Exception;
    List<RecipesDto> getRecipesByMenuId(Long menuId);

    List<MenuWithRecipeDTO> getMenuWithRecipes(Long menuId);

    MenuDTO findById(Long id);
    Map<Products, Map.Entry<Integer, Units>> calculateRequiredProductsForMenu (Long id);
    CreateMenuDto save(CreateMenuDto menuDTO);
    MenuDTO update(MenuDTO menuDTO);
    String delete(Long id);


}

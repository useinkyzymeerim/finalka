package com.finalka.service;


import com.finalka.dto.CreateMenuDto;
import com.finalka.dto.MenuDTO;
import com.finalka.dto.MenuWithRecipeDTO;
import com.finalka.dto.RecipesDto;

import java.util.List;
import java.util.Map;

public interface MenuService {

    List<MenuDTO> findAll() throws Exception;
    List<RecipesDto> getRecipesByMenuId(Long menuId);
    MenuDTO findById(Long id);
    List<Map<String, Object>>calculateRequiredProductsForMenu (Long id);
    CreateMenuDto save(CreateMenuDto menuDTO);
    MenuDTO update(MenuDTO menuDTO);
    String delete(Long id);
}
package com.finalka.service;


import com.finalka.entity.RecipesWithProducts;

import java.util.List;

public interface RecipesWithProductsService {
    RecipesWithProducts findById(Long id);
    List<RecipesWithProducts> findAll();
}

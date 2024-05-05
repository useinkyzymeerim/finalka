package com.finalka.service;

import com.finalka.dto.CreateProductOfShopDto;
import com.finalka.dto.ProductOfShopDto;

import java.util.Date;
import java.util.List;

public interface ProductOfShopService {
    CreateProductOfShopDto createProduct(CreateProductOfShopDto createProductOfShopDto) ;
    CreateProductOfShopDto updateProduct(Long productId, CreateProductOfShopDto productDTO);
    void deleteProduct(Long productId);
    ProductOfShopDto getProduct(Long productId);
    List<ProductOfShopDto> getAllProducts();
    void decreaseProductQuantityInStock(Long productId, int quantity);

    List<ProductOfShopDto> filterProductsByType(String type);

    List<ProductOfShopDto> filterProductsByAvailability(boolean inStock);

}

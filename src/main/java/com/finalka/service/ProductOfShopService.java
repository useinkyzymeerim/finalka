package com.finalka.service;

import com.finalka.dto.CreateProductOfShopDto;
import com.finalka.dto.ProductOfShopDto;
import com.finalka.exception.InvalidProductDataException;
import com.finalka.exception.ProductAlreadyExistsException;
import com.finalka.exception.ProductNotFoundException;

import java.util.List;

public interface ProductOfShopService {
    void createProduct(CreateProductOfShopDto createProductOfShopDto) throws InvalidProductDataException, ProductAlreadyExistsException;
    CreateProductOfShopDto updateProduct(Long productId, CreateProductOfShopDto productDTO) throws InvalidProductDataException;
    void deleteProduct(Long productId) throws ProductNotFoundException;
    ProductOfShopDto getProduct(Long productId);
    List<ProductOfShopDto> getAllProducts();
    void decreaseProductQuantityInStock(Long productId, int quantity);
    List<ProductOfShopDto> filterProductsByType(String type);
    List<ProductOfShopDto> filterProductsByAvailability(boolean inStock);
    ProductOfShopDto getProductByName(String productName);

}

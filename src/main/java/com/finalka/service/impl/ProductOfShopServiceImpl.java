package com.finalka.service.impl;

import com.finalka.dto.CreateProductOfShopDto;
import com.finalka.dto.ProductOfShopDto;
import com.finalka.entity.ProductOfShop;
import com.finalka.mapper.ProductMapper;
import com.finalka.repo.ProductOfShopRepo;
import com.finalka.service.ProductOfShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductOfShopServiceImpl implements ProductOfShopService {
    private final ProductOfShopRepo productOfShopRepo;
    private final ModelMapper modelMapper;

    @Override
    public void createProduct(CreateProductOfShopDto createProductOfShopDto) {
        ProductOfShop product = new ProductOfShop();
        product.setProductName(createProductOfShopDto.getProductName());
        product.setPrice(createProductOfShopDto.getPrice());
        product.setQuantity(createProductOfShopDto.getQuantity());
        product.setUnits2Enum(createProductOfShopDto.getUnits2Enum());
        product.setType(createProductOfShopDto.getType());
        product.setQuantityInStock(createProductOfShopDto.getQuantityInStock());

        product.updateInStock();

        ProductOfShop savedProduct = productOfShopRepo.save(product);

        CreateProductOfShopDto.builder()
                .id(savedProduct.getId())
                .productName(savedProduct.getProductName())
                .price(savedProduct.getPrice())
                .quantity(savedProduct.getQuantity())
                .units2Enum(savedProduct.getUnits2Enum())
                .type(savedProduct.getType())
                .quantityInStock(savedProduct.getQuantityInStock())
                .build();
    }

    public CreateProductOfShopDto updateProduct(Long productId, CreateProductOfShopDto productDTO) {
        ProductOfShop existingProduct = productOfShopRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));

        existingProduct.setProductName(productDTO.getProductName());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setQuantity(productDTO.getQuantity());
        existingProduct.setUnits2Enum(productDTO.getUnits2Enum());
        existingProduct.setType(productDTO.getType());
        existingProduct.setQuantityInStock(productDTO.getQuantityInStock());

        existingProduct.updateInStock();

        ProductOfShop updatedProduct = productOfShopRepo.save(existingProduct);

        CreateProductOfShopDto updatedProductDto = CreateProductOfShopDto.builder()
                .id(updatedProduct.getId())
                .productName(updatedProduct.getProductName())
                .price(updatedProduct.getPrice())
                .quantity(updatedProduct.getQuantity())
                .units2Enum(updatedProduct.getUnits2Enum())
                .type(updatedProduct.getType())
                .quantityInStock(updatedProduct.getQuantityInStock())
                .build();

        return updatedProductDto;
    }

    public void deleteProduct(Long productId) {
        ProductOfShop existingProduct = productOfShopRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));
        existingProduct.setDeleted(true);
        existingProduct.setDeletionTime(LocalDateTime.now());
        productOfShopRepo.save(existingProduct);
    }

    public ProductOfShopDto getProduct(Long productId) {
        ProductOfShop product = productOfShopRepo.findByIdAndDeletedFalse(productId)
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));
        return modelMapper.map(product, ProductOfShopDto.class);
    }

    @Transactional(readOnly = true)
    public ProductOfShopDto getProductByName(String productName) {
        ProductOfShop product = productOfShopRepo.findByProductNameIgnoreCaseContainingAndDeletedFalse(productName)
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));

        return modelMapper.map(product, ProductOfShopDto.class);
    }

    public List<ProductOfShopDto> getAllProducts() {
        List<ProductOfShop> products = productOfShopRepo.findAllByDeletedFalse();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductOfShopDto.class))
                .collect(Collectors.toList());
    }

    public void decreaseProductQuantityInStock(Long productId, int quantity) {
        ProductOfShop product = productOfShopRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Продукт с id: " + productId + " не найден!"));

        int currentQuantityInStock = product.getQuantityInStock();
        if (currentQuantityInStock < quantity) {
            throw new RuntimeException("Недостаточно количества средств на складе для товара с id: " + productId);
        }

        int newQuantityInStock = currentQuantityInStock - quantity;
        product.setQuantityInStock(newQuantityInStock);

        // Update the inStock status
        product.updateInStock();

        productOfShopRepo.save(product);
    }

    // Фильтрация продуктов по типу продукта (например, фрукты, овощи, мясо, рыба и т.д.)
    public List<ProductOfShopDto> filterProductsByType(String type) {
        List<ProductOfShop> filteredProducts = productOfShopRepo.findByType(type);
        return filteredProducts.stream()
                .map(product -> modelMapper.map(product, ProductOfShopDto.class))
                .collect(Collectors.toList());
    }

    // Фильтрация продуктов по наличию на складе
    public List<ProductOfShopDto> filterProductsByAvailability(boolean inStock) {
        List<ProductOfShop> filteredProducts = productOfShopRepo.findByInStock(inStock);
        return filteredProducts.stream()
                .map(product -> modelMapper.map(product, ProductOfShopDto.class))
                .collect(Collectors.toList());
    }

}

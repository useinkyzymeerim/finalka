package com.finalka.service.impl;

import com.finalka.dto.CreateProductOfShopDto;
import com.finalka.dto.ProductOfShopDto;
import com.finalka.entity.ProductOfShop;
import com.finalka.exception.InvalidProductDataException;
import com.finalka.exception.ProductAlreadyExistsException;
import com.finalka.exception.ProductNotFoundException;
import com.finalka.repo.ProductOfShopRepo;
import com.finalka.service.ProductOfShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
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
    @Transactional
    public Long createProduct(CreateProductOfShopDto createProductOfShopDto) throws InvalidProductDataException, ProductAlreadyExistsException {
        try {
            if (productOfShopRepo.existsByProductName(createProductOfShopDto.getProductName())) {
                throw new ProductAlreadyExistsException("Продукт с таким именем уже существует");
            }

            ProductOfShop product = new ProductOfShop();
            product.setProductName(createProductOfShopDto.getProductName());
            product.setImageBase64(createProductOfShopDto.getImageBase64());
            product.setPrice(createProductOfShopDto.getPrice());
            product.setQuantity(createProductOfShopDto.getQuantity());
            product.setUnits2Enum(createProductOfShopDto.getUnits2Enum());
            product.setType(createProductOfShopDto.getType());
            product.setQuantityInStock(createProductOfShopDto.getQuantityInStock());

            product.updateInStock();

            ProductOfShop savedProduct = productOfShopRepo.save(product);
            return savedProduct.getId();
        } catch (DataIntegrityViolationException e) {
            throw new InvalidProductDataException("Ошибка целостности данных: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать продукт: " + e.getMessage(), e);
        }
    }


    @Override
    @Transactional
    public CreateProductOfShopDto updateProduct(Long productId, CreateProductOfShopDto productDTO) throws InvalidProductDataException {
        try {
            ProductOfShop existingProduct = productOfShopRepo.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Продукт с id " + productId + " не найден"));

            existingProduct.setProductName(productDTO.getProductName());
            existingProduct.setImageBase64(productDTO.getImageBase64());
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setQuantity(productDTO.getQuantity());
            existingProduct.setUnits2Enum(productDTO.getUnits2Enum());
            existingProduct.setType(productDTO.getType());
            existingProduct.setQuantityInStock(productDTO.getQuantityInStock());

            existingProduct.updateInStock();

            ProductOfShop updatedProduct = productOfShopRepo.save(existingProduct);

            return CreateProductOfShopDto.builder()
                    .id(updatedProduct.getId())
                    .productName(updatedProduct.getProductName())
                    .imageBase64(updatedProduct.getImageBase64())
                    .price(updatedProduct.getPrice())
                    .quantity(updatedProduct.getQuantity())
                    .units2Enum(updatedProduct.getUnits2Enum())
                    .type(updatedProduct.getType())
                    .quantityInStock(updatedProduct.getQuantityInStock())
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new InvalidProductDataException("Ошибка целостности данных: " + e.getMostSpecificCause().getMessage(), e);
        }catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось обновить продукт", e);
        }
    }


    public void deleteProduct(Long productId) throws ProductNotFoundException {
        try {
            ProductOfShop existingProduct = productOfShopRepo.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Продукт с id " + productId + " не найден"));

            existingProduct.setDeleted(true);
            existingProduct.setDeletionTime(LocalDateTime.now());
            productOfShopRepo.save(existingProduct);
        }catch (ProductNotFoundException e) {
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("Не удалось удалить продукт", e);
        }
    }

    @Transactional
    public ProductOfShopDto getProduct(Long productId) {
        ProductOfShop product = productOfShopRepo.findByIdAndDeletedFalse(productId)
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));

        return ProductOfShopDto.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .units2Enum(product.getUnits2Enum())
                .quantityInStock(product.getQuantityInStock())
                .type(product.getType())
                .imageBase64(product.getImageBase64())
                .build();
    }

 @Transactional
    public List<ProductOfShopDto> getProductsByName(String productName) {
        List<ProductOfShop> products = productOfShopRepo.findByProductNameIgnoreCaseContainingAndDeletedFalse(productName);

        if (products.isEmpty()) {
            return Collections.emptyList();
        }

        return products.stream()
                .map(product -> modelMapper.map(product, ProductOfShopDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ProductOfShopDto> getAllProducts() {
        try {
            List<ProductOfShop> products = productOfShopRepo.findAllByDeletedFalse();
            return products.stream()
                    .map(product -> ProductOfShopDto.builder()
                            .id(product.getId())
                            .productName(product.getProductName())
                            .price(product.getPrice())
                            .quantity(product.getQuantity())
                            .units2Enum(product.getUnits2Enum())
                            .quantityInStock(product.getQuantityInStock())
                            .type(product.getType())
                            .imageBase64(product.getImageBase64())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new ServiceException("Ошибка при получении продуктов магазина", ex);
        }
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
    @Transactional(readOnly = true)
    public List<ProductOfShopDto> filterProductsByType(String type) {
        try {
            List<ProductOfShop> filteredProducts = productOfShopRepo.findByTypeIgnoreCase(type);
            return filteredProducts.stream()
                    .map(product -> modelMapper.map(product, ProductOfShopDto.class))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new ServiceException("Ошибка при фильтрации продуктов по типу", ex);
        }
    }


    // Фильтрация продуктов по наличию на складе
    @Transactional(readOnly = true)
    public List<ProductOfShopDto> filterProductsByAvailability(boolean inStock) {
        try {
            List<ProductOfShop> filteredProducts = productOfShopRepo.findByInStockAndDeletedFalse(inStock);
            return filteredProducts.stream()
                    .map(product -> modelMapper.map(product, ProductOfShopDto.class))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            // Обработка исключения
            throw new ServiceException("Ошибка при фильтрации продуктов по наличию", ex);
        }
    }
}
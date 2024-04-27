package com.finalka.service.impl;


import com.finalka.dto.MenuDTO;
import com.finalka.dto.ProductDTO;
import com.finalka.entity.Menu;
import com.finalka.entity.Products;
import com.finalka.repo.ProductRepo;
import com.finalka.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductsServiceImpl implements ProductService {
    private final ProductRepo productRepo;

    @Override
    public ProductDTO save(ProductDTO productDTO) {
        try {
            log.info("СТАРТ: ProductsServiceImpl - save() {}", productDTO);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Products products = Products.builder()
                    .productName(productDTO.getProductName())
                    .createdBy(username)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            productDTO.setId(productRepo.save(products).getId());
            productDTO.setCreatedAt(products.getCreatedAt());
            productDTO.setCreatedBy(username);

        } catch (Exception e) {
            log.error("Не удалось добавить продукт в базу данных");
            throw new RuntimeException("Не удалось добавить продукт в базу данных");
        }
        log.info("КОНЕЦ: ProductsServiceImpl - save {} ", productDTO);
        return productDTO;

    }

    @Override
    public String delete(Long id) {
        log.info("СТАРТ: ProductsServiceImpl - delete(). Удалить запись с id {}", id);
        Products products = productRepo.findByDeletedAtIsNullAndId(id);
        if (products == null) {
            log.error("Продукт с id " + id + " не найдена!");
            throw new NullPointerException("Продукт с id " + id + " не найдена!");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        products.setDeletedBy(username);
        products.setDeletedAt(new Timestamp(System.currentTimeMillis()));
        productRepo.save(products);
        log.info("КОНЕЦ: ProductsServiceImpl - delete(). Удаленна запись с id {}", id);
        return "Продукт с id " + id + " была удалена!";
    }

    @Override
    public ProductDTO findById(Long id) {
        log.info("СТАРТ: ProductsServiceImpl - findById({})", id);
        Products products = productRepo.findByDeletedAtIsNullAndId(id);
        if (products == null) {
            log.error("Продукт с id " + id + " не найдена!");
            throw new NullPointerException("Продукт с id " + id + " не найдена!");
        }
        log.info("КОНЕЦ: ProductsServiceImpl - findById(). Product - {} ", products);
        return ProductDTO.builder()
                .id(products.getId())
                .productName(products.getProductName())
                .createdBy(products.getCreatedBy())
                .createdAt(products.getCreatedAt())
                .lastUpdatedBy(products.getLastUpdatedBy())
                .lastUpdatedAt(products.getLastUpdatedAt())
                .deletedBy(products.getDeletedBy())
                .deletedAt(products.getDeletedAt())
                .build();

    }
    @Override
    public List<ProductDTO> findAll() {
        log.info("СТАРТ: ProductsServiceImpl - findAll()");
        List<Products> productsList = productRepo.findAllByDeletedAtIsNull();
        if (productsList.isEmpty()) {
            log.error("Актуальных продуктов нет!");
            throw new NullPointerException("Актуальных продуктов нет!");
        }
        List<ProductDTO> productDTOS = new ArrayList<>();
        for (Products products : productsList) {
            ProductDTO productDTO = ProductDTO.builder()
                    .id(products.getId())
                    .productName(products.getProductName())
                    .createdBy(products.getCreatedBy())
                    .createdAt(products.getCreatedAt())
                    .lastUpdatedBy(products.getLastUpdatedBy())
                    .lastUpdatedAt(products.getLastUpdatedAt())
                    .deletedBy(products.getDeletedBy())
                    .deletedAt(products.getDeletedAt())
                    .build();
            productDTOS.add(productDTO);
        }
        log.info("КОНЕЦ: ProductsServiceImpl - findAll()");
        return productDTOS;
    }
    @Override
    public ProductDTO update(ProductDTO productDTO) {
        log.info("СТАРТ: ProductsServiceImpl - update({})", productDTO);
        Products products = productRepo.findByDeletedAtIsNullAndId(productDTO.getId());
        if (products == null) {
            log.error("Продукт с id " + productDTO.getId() + " не найдена!");
            throw new NullPointerException("Продукт с id " + productDTO.getId() + " не найдена!");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();


        Products updatedProduct = Products.builder()
                .id(productDTO.getId())
                .productName(productDTO.getProductName())
                .createdBy(products.getCreatedBy())
                .createdAt(products.getCreatedAt())
                .lastUpdatedBy(username)
                .lastUpdatedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        productRepo.save(updatedProduct);

        productDTO.setCreatedBy(products.getCreatedBy());
        productDTO.setCreatedAt(products.getCreatedAt());
        productDTO.setLastUpdatedBy(username);
        productDTO.setLastUpdatedAt(updatedProduct.getLastUpdatedAt());

        log.info("КОНЕЦ: ProductsServiceImpl - update(). Обноленная запись - {}", productDTO);
        return productDTO;
    }

}

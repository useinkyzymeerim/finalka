package com.finalka.mapper.impl;

import com.finalka.dto.CreateProductOfShopDto;
import com.finalka.entity.ProductOfShop;
import com.finalka.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

    public class ProductMapperImpl implements ProductMapper {
        @Override
        public CreateProductOfShopDto toDto(ProductOfShop productOfShop) {
            CreateProductOfShopDto createProductOfShopDto = CreateProductOfShopDto.builder()
                    .id(productOfShop.getId())
                    .productName(productOfShop.getProductName())
                    .price(productOfShop.getPrice())
                    .quantity(productOfShop.getQuantity())
                    .units2Enum(productOfShop.getUnits2Enum())
                    .type(productOfShop.getType())
                    .quantityInStock(productOfShop.getQuantityInStock())
                    .build();
            return createProductOfShopDto;
        }


        @Override
        public ProductOfShop toEntity(CreateProductOfShopDto createProductOfShopDto) {
            ProductOfShop product = ProductOfShop.builder()
                    .id(createProductOfShopDto.getId())
                    .productName(createProductOfShopDto.getProductName())
                    .price(createProductOfShopDto.getPrice())
                    .quantity(createProductOfShopDto.getQuantity())
                    .units2Enum(createProductOfShopDto.getUnits2Enum())
                    .type(createProductOfShopDto.getType())
                    .quantityInStock(createProductOfShopDto.getQuantityInStock())
                    .build();
            return product;
        }

        @Override
        public List<CreateProductOfShopDto> toDtoList(List<ProductOfShop> product) {
            List<CreateProductOfShopDto> createProductOfShopDtos = new ArrayList<>();
            for (ProductOfShop productOfShop : product) {
                createProductOfShopDtos.add(toDto(productOfShop));
            }
            return createProductOfShopDtos;
        }
    }

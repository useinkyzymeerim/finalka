package com.finalka.mapper;

import com.finalka.dto.CreateProductOfShopDto;
import com.finalka.dto.RoleDto;
import com.finalka.entity.ProductOfShop;
import com.finalka.entity.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    CreateProductOfShopDto toDto(ProductOfShop productOfShop);
    ProductOfShop toEntity(CreateProductOfShopDto createProductOfShopDto);
    List<CreateProductOfShopDto> toDtoList(List<ProductOfShop> role);
}

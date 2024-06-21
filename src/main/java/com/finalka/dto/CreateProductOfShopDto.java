package com.finalka.dto;

import com.finalka.enums.Units2;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProductOfShopDto {
    private Long id;
    @NotBlank(message = "Наименование продукта не должно быть пустым")
    private String productName;

    private byte[] imageBase64;

    @NotNull(message = "Цена не должна быть пустой")
    @DecimalMin(value = "0.0", message = "Цена должна быть больше или равна 0")
    private Double price;

    @NotNull(message = "Количество не должно быть пустым")
    @Min(value = 0, message = "Количество должно быть не меньше 0")
    private Integer quantity;

    @NotNull(message = "Единица измерения не должна быть пустой")
    private Units2 units2Enum;

    @NotBlank(message = "Тип продукта не должен быть пустым")
    private String type;

    @NotNull(message = "Количество в наличии не должно быть пустым")
    @Min(value = 0, message = "Количество в наличии должно быть не меньше 0")
    private Integer quantityInStock;
}


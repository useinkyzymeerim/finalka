package com.finalka.dto;

import com.finalka.enums.Units;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateDTO {
    @NotNull
    private Long id;

    @NotNull(message = "Название продукта не должно быть пустым")
    @Size(min = 1, max = 255, message = "Название продукта должно содержать от 1 до 255 символов")
    private String productName;

    @NotNull(message = "Количество не должно быть пустым")
    @Min(value = 1, message = "Количество должно быть не менее 1")
    private Integer quantity;

    @Enumerated(value = EnumType.STRING)
    private Units unitsEnum;
}
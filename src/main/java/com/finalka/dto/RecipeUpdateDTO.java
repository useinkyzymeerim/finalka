package com.finalka.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeUpdateDTO {
    @NotNull
    private Long id;

    @NotNull(message = "Название блюда не должно быть пустым")
    @Size(min = 1, max = 255, message = "Название блюда должно содержать от 1 до 255 символов")
    private String nameOfFood;

    @NotNull(message = "Описание не должно быть пустым")
    @Size(min = 1, message = "Описание должно содержать хотя бы один символ")
    private String description;

    private byte[] imageBase64;

    @URL(message = "Ссылка на видео должна быть корректной URL")
    private String linkOfVideo;

    @NotNull(message = "Количество продукта не должно быть пустым")
    @Min(value = 1, message = "Количество продукта должно быть не менее 1")
    private Integer quantityOfProduct;

    @NotNull(message = "Поле порции не должно быть пустым" )
    private Integer portion;

    @NotNull(message = "Время приготовления не должно быть пустым")
    @Min(value = 1, message = "Время приготовления должно быть не менее 1 минуты")
    private Integer cookingTime;

    private List<ProductUpdateDTO> products;
}
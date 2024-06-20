package com.finalka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipesSearchDto {
    private String nameOfFood;
    private byte[] imageBase64;
    private String createdBy;
}

package com.finalka.dto;

import com.finalka.entity.ProductOfShop;
import com.finalka.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCartDto {
    private Long id;
    private Long userId;
}

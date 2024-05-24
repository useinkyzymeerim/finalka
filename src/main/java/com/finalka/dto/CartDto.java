package com.finalka.dto;

import com.finalka.entity.ProductOfShop;
import com.finalka.entity.Purchase;
import com.finalka.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public class CartDto {
        private List<CreateProductOfShopDto> productOfShops;
        private User user;
        private double totalPrice;
        private Purchase purchase;

    }

package com.finalka.dto;

import com.finalka.entity.Menu;
import com.finalka.entity.User;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeWithProductDTO {
    private Long id;
    private String nameOfFood;
    private String description;
    private byte[] imageBase64;
    private String linkOfVideo;
    private Integer quantityOfProduct;
    private Integer cookingTime;
    private Long menuId;
    private Long userId;
    private List<ProductDTO> products;
    private String createdBy;
    private Timestamp createdAt;
    private String lastUpdatedBy;
    private Timestamp lastUpdatedAt;
    private String deletedBy;
    private Timestamp deletedAt;
}
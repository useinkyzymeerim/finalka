package com.finalka.repo;

import com.finalka.entity.ProductOfShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProductOfShopRepo extends JpaRepository<ProductOfShop, Long> {
    List<ProductOfShop> findAllByDeletedFalse();
    Optional<ProductOfShop> findByIdAndDeletedFalse(Long id);

      List<ProductOfShop> findByType(String type);

    List<ProductOfShop> findByInStockAndDeletedFalse(boolean inStock);
    List<ProductOfShop> findByProductNameIgnoreCaseContainingAndDeletedFalse(String productName);

    boolean existsByProductName(String productName);
}

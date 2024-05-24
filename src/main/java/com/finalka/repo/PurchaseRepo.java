package com.finalka.repo;

import com.finalka.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseRepo extends JpaRepository<Purchase,Long> {
    @Query("SELECT p FROM Purchase p JOIN FETCH p.purchasedProducts WHERE p.id = :purchaseId")
    Purchase findByIdWithProducts(@Param("purchaseId") Long purchaseId);
}

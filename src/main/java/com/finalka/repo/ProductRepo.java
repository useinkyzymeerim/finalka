package com.finalka.repo;

import com.finalka.entity.Menu;
import com.finalka.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Products,Long> {
    List<Products> findAllByDeletedAtIsNull();
    Products findByDeletedAtIsNullAndId(Long id);
}
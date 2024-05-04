package com.finalka.repo;

import com.finalka.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepo extends JpaRepository<Cart,Long> {
    Optional<Object> findByUserId(Long userId);

}

package com.finalka.repo;

import com.finalka.entity.ProductOfShop;
import com.finalka.entity.Products;
import com.finalka.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<User> findByResetToken(String resetToken);
    Optional<User> findByEmail(String email);
}

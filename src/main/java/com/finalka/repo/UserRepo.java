package com.finalka.repo;

import com.finalka.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    User findByUsername(String username);
    Optional<User> findUserByRemoveDateIsNullAndId(Long id);
    List<User> findAllAndBOrderByRemoveDateIsNull();
}

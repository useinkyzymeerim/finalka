package com.finalka.repo;

import com.finalka.entity.Favorite;
import com.finalka.entity.Menu;
import com.finalka.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepo extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    List<Favorite> findByUser(User user);
    boolean existsByUserAndMenu(User user, Menu menu);
}

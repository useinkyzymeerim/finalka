package com.finalka.repo;

import com.finalka.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepo extends JpaRepository<Menu,Long> {

    List<Menu> findAllByDeletedAtIsNull();
    Optional<Menu> findByDeletedAtIsNullAndId(Long id);
}
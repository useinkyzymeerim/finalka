package com.finalka.repo;

import com.finalka.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role,Long> {
    Role findByNameIgnoreCase(String name);
}

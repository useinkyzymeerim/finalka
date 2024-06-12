package com.finalka.service;

import com.finalka.dto.RoleDto;
import com.finalka.entity.Role;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RoleService {
    ResponseEntity<Long> save(RoleDto role);
    List<RoleDto> allRoles();
    RoleDto findById(Long id);
    RoleDto findByName(String name);
    ResponseEntity<String> update(Long id, Role role);
    ResponseEntity<String> delete(Long id);
}


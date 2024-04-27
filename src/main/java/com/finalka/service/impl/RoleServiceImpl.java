package com.finalka.service.impl;

import com.finalka.dto.RoleDto;
import com.finalka.entity.Role;
import com.finalka.mapper.RoleMapper;
import com.finalka.repo.RoleRepo;
import com.finalka.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepo roleRepo;
    private final RoleMapper roleMapper;

    @Override
    public ResponseEntity<Long> save(RoleDto roleDto) {
        Role savedRole = roleRepo.save(roleMapper.toEntity(roleDto));
        return new ResponseEntity<>(savedRole.getId(), HttpStatus.CREATED);
    }

    @Override
    public List<RoleDto> allRoles() {
        return roleMapper.toDtoList(roleRepo.findAll());
    }

    @Override
    public RoleDto findById(Long id) {
        Role role = roleRepo.findById(id).orElse(null);
        if (role == null) {
            return null;
        }
        return roleMapper.toDto(role);
    }

    @Override
    public RoleDto findByName(String name) {
        Role role = roleRepo.findByNameIgnoreCase(name);
        if (role == null) {
            throw new EntityNotFoundException("Роль не найден");
        }
        return roleMapper.toDto(role);
    }

    @Override
    public ResponseEntity<String> update(Long id, Role role) {
        Optional<Role> optionalRole = roleRepo.findById(id);
        if (optionalRole.isPresent()) {
            role.setId(id);
            roleRepo.save(role);
            return new ResponseEntity<>("Роль успешно обновлен", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Роль не найден", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<String> delete(Long id) {
        Optional<Role> optionalRole = roleRepo.findById(id);
        if (optionalRole.isPresent()) {
            roleRepo.deleteById(id);
            return new ResponseEntity<>("Роль успешно удален", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Роль не найден", HttpStatus.NOT_FOUND);
        }
    }
}
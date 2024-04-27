package com.finalka.dto;

import com.finalka.entity.Recipes;
import com.finalka.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String surname;
    private String username;
    private String password;
    private Date removeDate;
    private Set<Role> roles;

    private LocalDateTime lastAuthentication;
    private String phoneNumber;
    private Set<Recipes> recipes;
}
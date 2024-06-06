package com.finalka.dto;

import com.finalka.entity.Role;
import com.finalka.validations.ValidPassword;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Data
@Builder
public class UserDto {
    private Long id;

    @NotNull(message = "Имя не должно быть пустым")
    private String name;

    @NotNull(message = "Фамилия не должна быть пустой")
    private String surname;

    @NotNull(message = "Имя пользователя не должно быть пустым")
    private String username;

    // @ValidPassword
    private String password;
    @NotNull(message = "Email не должен быть пустым")
    private String email;
    private Date removeDate;
    private Set<Role> roles;

    private LocalDateTime lastAuthentication;


}
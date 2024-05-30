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
    @NotNull
    private String name;
    @NotNull
    private String surname;
    @NotNull
    private String username;
    @ValidPassword
    private String password;
    private String email;
    private Date removeDate;
    private Set<Role> roles;

    private LocalDateTime lastAuthentication;

    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number")
    private String phoneNumber;
}
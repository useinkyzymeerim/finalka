package com.finalka.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserDto {
    @NotNull(message = "Имя не должно быть пустым")
    private String name;

    @NotNull(message = "Фамилия не должна быть пустой")
    private String surname;

    @NotNull(message = "Имя пользователя не должно быть пустым")
    private String username;

    private String email;

    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Неверный номер телефона")
    private String phoneNumber;
}

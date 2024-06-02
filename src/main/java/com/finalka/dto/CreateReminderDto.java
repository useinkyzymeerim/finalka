package com.finalka.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReminderDto {
    @NotNull(message = "это поле не должн быть пустым!")
    @Min(value = 0, message = "Час должен быть в диапазоне от 0 до 23")
    @Max(value = 23, message = "Час должен быть в диапазоне от 0 до 23")
    private Integer hour;

    @NotNull(message = "это поле не должн быть пустым!")
    @Min(value = 0, message = "Минута должен быть в диапазоне от 0 до 59")
    @Max(value = 59, message = "Минута должен быть в диапазоне от 0 до 59")
    private Integer minute;

    @NotBlank(message = "это поле не должн быть пустым!")
    @Size(max = 255, message = "Длина сообщения не должна превышать 255 символов")
    private String message;
}

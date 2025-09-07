package org.test.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Запрос аутентификации")
@Data
public class AuthRequest {

    @Schema(description = "Имя пользователя", example = "john_doe", required = true)
    @NotBlank
    private String username;

    @Schema(description = "Пароль", example = "password123", required = true)
    @NotBlank
    private String password;
}
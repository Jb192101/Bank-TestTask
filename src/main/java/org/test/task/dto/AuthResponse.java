package org.test.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Ответ аутентификации")
@Data
public class AuthResponse {
    @Schema(description = "JWT токен", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Роль пользователя", example = "ROLE_USER")
    private String role;

    @Schema(description = "Имя пользователя", example = "john_doe")
    private String username;
}

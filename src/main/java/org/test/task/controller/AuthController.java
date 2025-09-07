package org.test.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.test.task.dto.AuthRequest;
import org.test.task.dto.AuthResponse;
import org.test.task.entity.Role;
import org.test.task.entity.User;
import org.test.task.repository.UserRepository;
import org.test.task.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для аутентификации и регистрации")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Operation(
            summary = "Вход в систему",
            description = "Аутентификация пользователя и получение JWT токена"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешная аутентификация",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Неверные учетные данные"
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            User user = userRepository.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDetails userDetails = user.toUserDetails();
            final String jwt = jwtUtil.generateToken(userDetails);

            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            response.put("role", user.getRole().name());
            response.put("username", user.getUsername());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }
    }

    @Operation(
            summary = "Регистрация пользователя",
            description = "Создание нового пользователя с ролью USER"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно зарегистрирован"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Имя пользователя уже существует"
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest authRequest) {
        if (userRepository.existsByUsername(authRequest.getUsername())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Username already exists");
            return ResponseEntity.badRequest().body(response);
        }

        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setActive(true);

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }
}

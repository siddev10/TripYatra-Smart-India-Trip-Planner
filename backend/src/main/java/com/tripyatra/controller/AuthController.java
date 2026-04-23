package com.tripyatra.controller;

import com.tripyatra.dto.AuthResponse;
import com.tripyatra.dto.LoginRequest;
import com.tripyatra.dto.RegisterRequest;
import com.tripyatra.model.User;
import com.tripyatra.service.AuthService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication Controller — handles registration, login, and profile endpoints.
 * Demonstrates: REST Controller, Dependency Injection, HTTP Status codes, Validation
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/register — Create a new user account
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request);
            String token = authService.generateToken(user);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(AuthResponse.success("Account created!", token, user.getId(), user.getName(), user.getEmail()));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/auth/login — Authenticate and receive JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = authService.login(request);
            String token = authService.generateToken(user);

            return ResponseEntity.ok(
                    AuthResponse.success("Login successful", token, user.getId(), user.getName(), user.getEmail()));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/auth/me — Get the currently authenticated user's profile
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        Claims claims = (Claims) authentication.getPrincipal();
        Map<String, Object> user = Map.of(
                "id", Long.parseLong(claims.getSubject()),
                "name", claims.get("name", String.class),
                "email", claims.get("email", String.class)
        );
        return ResponseEntity.ok(Map.of("user", user));
    }
}

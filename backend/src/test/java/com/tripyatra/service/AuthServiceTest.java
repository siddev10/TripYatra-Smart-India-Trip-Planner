package com.tripyatra.service;

import com.tripyatra.dto.LoginRequest;
import com.tripyatra.dto.RegisterRequest;
import com.tripyatra.model.User;
import com.tripyatra.repository.UserRepository;
import com.tripyatra.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService — tests business logic in isolation.
 * Demonstrates: JUnit 5, Mockito, Unit testing best practices, TDD
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("John Doe", "john@example.com", "password123");
        loginRequest = new LoginRequest("john@example.com", "password123");

        testUser = new User("John Doe", "john@example.com", "hashed_password");
        testUser.setId(1L);
    }

    @Test
    @DisplayName("register() — should create user with hashed password")
    void testRegisterSuccess() {
        // Arrange
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = authService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());

        // Verify interactions
        verify(userRepository).existsByEmail("john@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register() — should throw exception for duplicate email")
    void testRegisterDuplicateEmail() {
        // Arrange
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Email already registered", exception.getMessage());
        verify(userRepository, never()).save(any()); // Should not attempt to save
    }

    @Test
    @DisplayName("login() — should authenticate user with valid credentials")
    void testLoginSuccess() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);

        // Act
        User result = authService.login(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    @DisplayName("login() — should throw exception for non-existent email")
    void testLoginUserNotFound() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("login() — should throw exception for wrong password")
    void testLoginWrongPassword() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    @DisplayName("generateToken() — should delegate to JwtUtil")
    void testGenerateToken() {
        // Arrange
        when(jwtUtil.generateToken(testUser)).thenReturn("mock_jwt_token");

        // Act
        String token = authService.generateToken(testUser);

        // Assert
        assertEquals("mock_jwt_token", token);
        verify(jwtUtil).generateToken(testUser);
    }
}

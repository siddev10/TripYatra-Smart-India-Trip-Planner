package com.tripyatra.service;

import com.tripyatra.dto.LoginRequest;
import com.tripyatra.dto.RegisterRequest;
import com.tripyatra.model.User;
import com.tripyatra.repository.UserRepository;
import com.tripyatra.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Authentication Service — handles user registration and login.
 * Demonstrates: Service Layer pattern, Dependency Injection, BCrypt hashing
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /** Constructor-based Dependency Injection (Spring best practice) */
    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Register a new user.
     * @throws RuntimeException if email already exists
     */
    public User register(RegisterRequest request) {
        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Hash the password with BCrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create and persist the new user entity
        User user = new User(request.getName(), request.getEmail(), hashedPassword);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user); // Hibernate persists to the database
    }

    /**
     * Authenticate a user and return the user entity.
     * @throws RuntimeException if credentials are invalid
     */
    public User login(LoginRequest request) {
        // Look up user by email
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = optionalUser.get();

        // Verify password against BCrypt hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }

    /**
     * Generate JWT token for an authenticated user.
     */
    public String generateToken(User user) {
        return jwtUtil.generateToken(user);
    }

    /**
     * Find user by ID — used for the /me endpoint.
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}

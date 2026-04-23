package com.tripyatra.dto;

import java.util.Map;

/**
 * DTO for authentication responses (login/register).
 * Matches the frontend's expected JSON structure.
 */
public class AuthResponse {

    private String message;
    private String token;
    private Map<String, Object> user;

    public AuthResponse() {}

    public AuthResponse(String message, String token, Map<String, Object> user) {
        this.message = message;
        this.token = token;
        this.user = user;
    }

    /** Factory method for successful auth */
    public static AuthResponse success(String message, String token, Long id, String name, String email) {
        Map<String, Object> userMap = Map.of(
            "id", id,
            "name", name,
            "email", email
        );
        return new AuthResponse(message, token, userMap);
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Map<String, Object> getUser() { return user; }
    public void setUser(Map<String, Object> user) { this.user = user; }
}

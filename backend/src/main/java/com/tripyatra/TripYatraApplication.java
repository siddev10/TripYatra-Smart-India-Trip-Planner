package com.tripyatra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TripYatra — Smart India Trip Planner
 * Spring Boot Application Entry Point
 *
 * This application provides a REST API for:
 * - User authentication (JWT-based)
 * - Trip planning and management (CRUD)
 * - Train search via Indian Rail API + AI fallback
 * - AI-powered itinerary generation
 * - Travel package browsing
 */
@SpringBootApplication
public class TripYatraApplication {

    public static void main(String[] args) {
        SpringApplication.run(TripYatraApplication.class, args);
    }
}

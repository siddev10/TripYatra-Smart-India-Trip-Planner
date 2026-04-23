package com.tripyatra.controller;

import com.tripyatra.dto.TripRequest;
import com.tripyatra.model.Trip;
import com.tripyatra.service.TripService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Trip Controller — CRUD operations for user trips.
 * Demonstrates: RESTful endpoints, CRUD via JPA, Authentication context
 */
@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    /**
     * POST /api/trips — Create and save a new trip
     */
    @PostMapping
    public ResponseEntity<?> createTrip(@RequestBody TripRequest request, Authentication auth) {
        Long userId = extractUserId(auth);

        if (request.getSource() == null || request.getDestination() == null || request.getDate() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Source, destination and date required"));
        }

        Trip trip = tripService.createTrip(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Trip saved!", "trip", trip));
    }

    /**
     * GET /api/trips — Get all trips for the authenticated user
     */
    @GetMapping
    public ResponseEntity<?> getUserTrips(Authentication auth) {
        Long userId = extractUserId(auth);
        List<Trip> trips = tripService.getUserTrips(userId);
        return ResponseEntity.ok(Map.of("trips", trips));
    }

    /**
     * DELETE /api/trips/{id} — Delete a specific trip
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrip(@PathVariable Long id, Authentication auth) {
        Long userId = extractUserId(auth);
        boolean deleted = tripService.deleteTrip(id, userId);

        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Trip deleted"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Trip not found"));
    }

    /** Extract user ID from JWT claims in the authentication context */
    private Long extractUserId(Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        return Long.parseLong(claims.getSubject());
    }
}

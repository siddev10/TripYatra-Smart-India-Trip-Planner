package com.tripyatra.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Flight Controller — simulated flight search endpoint.
 * Demonstrates: REST endpoint, Data generation logic
 */
@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private static final String[] AIRLINES = {"IndiGo", "Air India", "Vistara", "Akasa Air", "SpiceJet"};

    /**
     * POST /api/flights/search — Simulated flight search
     */
    @PostMapping("/search")
    public Map<String, Object> searchFlights(@RequestBody Map<String, String> request) {
        String source = request.getOrDefault("source", "");
        String destination = request.getOrDefault("destination", "");

        List<Map<String, Object>> flights = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> flight = new LinkedHashMap<>();
            flight.put("flight_number", "6E-" + (100 + i));
            flight.put("airline", AIRLINES[i]);
            flight.put("source", source);
            flight.put("destination", destination);
            flight.put("departure", (10 + i) + ":00");
            flight.put("arrival", (12 + i) + ":30");
            flight.put("duration", "2h 30m");
            flight.put("price", 4500 + (i * 800));
            flight.put("available_seats", 10 + (i * 5));
            flights.add(flight);
        }

        return Map.of("flights", flights);
    }
}

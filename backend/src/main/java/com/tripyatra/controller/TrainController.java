package com.tripyatra.controller;

import com.tripyatra.dto.TrainSearchRequest;
import com.tripyatra.service.TrainService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Train Controller — train search and station lookup endpoints.
 * Demonstrates: REST Controller, External API calls, Resource loading
 */
@RestController
@RequestMapping("/api")
public class TrainController {

    private final TrainService trainService;

    @Value("${rail.api.key:}")
    private String railApiKey;

    @Value("${rail.api.base-url:http://indianrailapi.com/api/v2}")
    private String railBaseUrl;

    @Autowired
    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    /**
     * POST /api/trains/search — Search for trains between two stations
     */
    @PostMapping("/trains/search")
    public ResponseEntity<?> searchTrains(@Valid @RequestBody TrainSearchRequest request) {
        Map<String, Object> result = trainService.searchTrains(
                request.getSource(),
                request.getDestination(),
                request.getDate(),
                request.getTravellers()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/stations/all — Serve the complete stations JSON file (8400+ stations)
     */
    @GetMapping(value = "/stations/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllStations() throws IOException {
        Resource resource = new ClassPathResource("all-stations.json");
        String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return ResponseEntity.ok(content);
    }

    /**
     * GET /api/stations/search?q=... — Station autocomplete via Rail API
     */
    @GetMapping("/stations/search")
    public ResponseEntity<?> searchStations(@RequestParam String q) {
        if (railApiKey == null || railApiKey.isEmpty()) {
            return ResponseEntity.ok(Map.of("stations", List.of()));
        }

        try {
            String url = String.format("%s/AutoCompleteStation/apikey/%s/StationName/%s",
                    railBaseUrl, railApiKey, q);
            RestTemplate restTemplate = new RestTemplate();
            Map response = restTemplate.getForObject(url, Map.class);
            Object stations = response != null ? response.getOrDefault("Stations", response.get("Station")) : List.of();
            return ResponseEntity.ok(Map.of("stations", stations != null ? stations : List.of()));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("stations", List.of()));
        }
    }
}

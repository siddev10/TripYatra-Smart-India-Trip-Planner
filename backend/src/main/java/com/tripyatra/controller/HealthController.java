package com.tripyatra.controller;

import com.tripyatra.repository.TripRepository;
import com.tripyatra.repository.UserRepository;
import com.tripyatra.service.StationDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Health Controller — server status and diagnostics endpoint.
 * Demonstrates: Dependency Injection, System monitoring
 */
@RestController
public class HealthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private StationDataService stationDataService;

    @Value("${rail.api.key:}")
    private String railApiKey;

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    /**
     * GET /api/health — Check server health and configuration status
     */
    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("status", "ok");
        status.put("framework", "Spring Boot 3.2 + JPA/Hibernate");
        status.put("rail_api_key_set", railApiKey != null && !railApiKey.isEmpty());
        status.put("rail_api_key_preview", railApiKey != null && railApiKey.length() > 12
                ? railApiKey.substring(0, 12) + "..." : "NOT SET");
        status.put("openai_key_set", openaiApiKey != null && !openaiApiKey.isEmpty());
        status.put("gemini_key_set", geminiApiKey != null && !geminiApiKey.isEmpty());
        status.put("users", userRepository.count());
        status.put("trips", tripRepository.count());
        status.put("station_codes", stationDataService.getStationCodeCount());
        status.put("packages_count", 6);
        return status;
    }
}

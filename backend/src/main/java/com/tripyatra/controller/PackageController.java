package com.tripyatra.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripyatra.dto.ItineraryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Package & Itinerary Controller — travel packages and AI itinerary generation.
 * Demonstrates: Static data management, AI API integration, Complex response building
 */
@RestController
@RequestMapping("/api")
public class PackageController {

    private static final Logger log = LoggerFactory.getLogger(PackageController.class);

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** Static travel packages data */
    private static final List<Map<String, Object>> PACKAGES = List.of(
        Map.of("id",1, "name","Golden Triangle", "cities",List.of("Delhi","Agra","Jaipur"), "duration","5 Days", "price",15000, "image","🏯", "description","Explore the heritage of North India."),
        Map.of("id",2, "name","God's Own Country", "cities",List.of("Kochi","Munnar","Alleppey"), "duration","6 Days", "price",18000, "image","🌴", "description","Experience the backwaters and hills of Kerala."),
        Map.of("id",3, "name","Royal Rajasthan", "cities",List.of("Jaipur","Jodhpur","Udaipur"), "duration","7 Days", "price",22000, "image","🏰", "description","A journey through the land of Maharajas."),
        Map.of("id",4, "name","Himalayan Escape", "cities",List.of("Chandigarh","Shimla","Manali"), "duration","6 Days", "price",14000, "image","🏔️", "description","Snow-capped peaks and serene valleys."),
        Map.of("id",5, "name","Goa Beach Party", "cities",List.of("Goa"), "duration","4 Days", "price",12000, "image","🏖️", "description","Sun, sand, and non-stop fun."),
        Map.of("id",6, "name","Spiritual Varanasi", "cities",List.of("Varanasi","Lucknow"), "duration","4 Days", "price",10000, "image","🪔", "description","The oldest living city in the world.")
    );

    /**
     * GET /api/packages — List all travel packages
     */
    @GetMapping("/packages")
    public ResponseEntity<?> getPackages() {
        return ResponseEntity.ok(Map.of("packages", PACKAGES));
    }

    /**
     * POST /api/itinerary/generate — Generate an AI-powered travel itinerary
     */
    @PostMapping("/itinerary/generate")
    public ResponseEntity<?> generateItinerary(@RequestBody ItineraryRequest request) {
        // Find the selected package (if any)
        Map<String, Object> pkg = null;
        if (request.getPackageId() != null) {
            pkg = PACKAGES.stream()
                    .filter(p -> p.get("id").equals(request.getPackageId()))
                    .findFirst().orElse(null);
        }

        if (pkg == null && request.getSource() == null && request.getDestination() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Package or Source/Destination required"));
        }

        @SuppressWarnings("unchecked")
        List<String> pkgCities = pkg != null ? (List<String>) pkg.get("cities") : List.of();
        String finalDest = pkg != null ? pkgCities.get(pkgCities.size() - 1) : request.getDestination();
        String finalSrc = request.getSource() != null ? request.getSource() : "Mumbai";
        int tripDays = request.getDays() != null ? request.getDays()
                : (pkg != null ? Integer.parseInt(((String) pkg.get("duration")).replaceAll("\\D+", "")) : 3);

        String prompt = buildItineraryPrompt(finalSrc, finalDest, tripDays, request, pkg, pkgCities);

        // Try OpenAI
        if (openaiApiKey != null && !openaiApiKey.isEmpty()) {
            try {
                log.info("[AI] Trying OpenAI for itinerary: {} -> {}", finalSrc, finalDest);
                Map<String, Object> aiData = callOpenAI(prompt);
                return ResponseEntity.ok(buildItineraryResponse(finalSrc, finalDest, tripDays, request, aiData, "OpenAI"));
            } catch (Exception e) {
                log.warn("[AI] OpenAI itinerary failed: {}", e.getMessage());
            }
        }

        // Try Gemini
        if (geminiApiKey != null && !geminiApiKey.isEmpty()) {
            String[] models = {"gemini-2.5-flash", "gemini-2.0-flash", "gemini-flash-latest", "gemini-pro-latest"};
            for (String model : models) {
                try {
                    log.info("[AI] Trying Gemini {} for itinerary", model);
                    Map<String, Object> aiData = callGemini(prompt, model);
                    return ResponseEntity.ok(buildItineraryResponse(finalSrc, finalDest, tripDays, request, aiData, "Gemini (" + model + ")"));
                } catch (Exception e) {
                    log.warn("[AI] Gemini {} failed: {}", model, e.getMessage());
                }
            }
        }

        // Fallback: manual itinerary
        return ResponseEntity.ok(generateFallbackItinerary(finalSrc, finalDest, tripDays, request, pkg, pkgCities));
    }

    // ── AI Call Methods ─────────────────────────────────

    private Map<String, Object> callOpenAI(String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> body = Map.of(
            "model", "gpt-4o-mini",
            "messages", List.of(
                Map.of("role", "system", "content", "You are a professional Indian travel consultant. Return valid JSON."),
                Map.of("role", "user", "content", prompt)
            ),
            "response_format", Map.of("type", "json_object")
        );

        ResponseEntity<JsonNode> response = restTemplate.exchange(
            "https://api.openai.com/v1/chat/completions", HttpMethod.POST,
            new HttpEntity<>(body, headers), JsonNode.class
        );

        String content = response.getBody().path("choices").get(0).path("message").path("content").asText();
        return objectMapper.readValue(content, new TypeReference<>() {});
    }

    private Map<String, Object> callGemini(String prompt, String model) throws Exception {
        String url = String.format(
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", model, geminiApiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
            "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt + "\nReturn ONLY valid JSON."))))
        );

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), JsonNode.class);
        String text = response.getBody().path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        String cleaned = text.replaceAll("```json", "").replaceAll("```", "").trim();
        return objectMapper.readValue(cleaned, new TypeReference<>() {});
    }

    // ── Response Builders ───────────────────────────────

    private Map<String, Object> buildItineraryResponse(String src, String dest, int days,
                                                        ItineraryRequest req, Map<String, Object> aiData, String provider) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("source", src);
        summary.put("destination", dest);
        summary.put("days", days);
        summary.put("travellers", req.getTravellers());
        summary.put("budget", req.getBudget());
        summary.put("accommodation", req.getAccommodation());
        summary.put("suggestedTransport", aiData.getOrDefault("suggestedTransport", "Train"));
        summary.put("transportReason", aiData.getOrDefault("transportReason", ""));
        summary.put("isAI", true);
        summary.put("provider", provider);

        return Map.of("summary", summary, "itinerary", aiData.getOrDefault("itinerary", List.of()));
    }

    private Map<String, Object> generateFallbackItinerary(String src, String dest, int days,
                                                           ItineraryRequest req, Map<String, Object> pkg, List<String> pkgCities) {
        String transport = "Train";
        String transportReason = "Budget-friendly and scenic.";
        if (req.getBudget() != null) {
            try {
                int budget = Integer.parseInt(req.getBudget());
                int travellers = req.getTravellers() != null ? req.getTravellers() : 1;
                if (budget / travellers > 10000) {
                    transport = "Flight";
                    transportReason = "Faster and fits your premium budget.";
                }
            } catch (NumberFormatException ignored) {}
        }

        List<List<Map<String, String>>> activityPool = List.of(
            List.of(
                Map.of("time","09:00 AM","activity","Breakfast at a local heritage cafe"),
                Map.of("time","11:00 AM","activity","Visit to the most famous local landmark/monument"),
                Map.of("time","01:30 PM","activity","Lunch at a highly-rated traditional restaurant"),
                Map.of("time","04:00 PM","activity","Exploring the local markets and shopping for souvenirs"),
                Map.of("time","08:00 PM","activity","Dinner featuring regional specialties")
            ),
            List.of(
                Map.of("time","08:30 AM","activity","Morning walk through a scenic park or garden"),
                Map.of("time","10:30 AM","activity","Visit to a local museum or art gallery"),
                Map.of("time","01:00 PM","activity","Quick lunch at a popular street food hub"),
                Map.of("time","03:30 PM","activity","Guided tour of historical sites or spiritual places"),
                Map.of("time","07:30 PM","activity","Evening cultural show or light & sound performance")
            ),
            List.of(
                Map.of("time","09:30 AM","activity","Leisurely breakfast and local area exploration"),
                Map.of("time","11:30 AM","activity","Visit to nearby architectural wonders"),
                Map.of("time","02:00 PM","activity","Relaxing lunch with a view"),
                Map.of("time","04:30 PM","activity","Photography session at scenic viewpoints"),
                Map.of("time","08:30 PM","activity","Fine dining experience at a top-rated hotel")
            ),
            List.of(
                Map.of("time","08:00 AM","activity","Early morning excursion to outskirts or nature spots"),
                Map.of("time","11:00 AM","activity","Workshop or interaction with local artisans"),
                Map.of("time","01:30 PM","activity","Authentic home-style meal at a local's place"),
                Map.of("time","04:00 PM","activity","Boating or evening stroll by the riverside/lake"),
                Map.of("time","07:00 PM","activity","Casual dinner and packing for the next day")
            )
        );

        List<Map<String, Object>> itinerary = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            String dayCity = (pkg != null && !pkgCities.isEmpty()) ? pkgCities.get((i - 1) % pkgCities.size()) : dest;
            Map<String, Object> day = new LinkedHashMap<>();
            day.put("day", i);
            day.put("title", "Day " + i + ": " + (i == 1 ? "Arrival & " : "") + "Exploring " + dayCity);
            day.put("events", new ArrayList<>(activityPool.get((i - 1) % activityPool.size())));
            itinerary.add(day);
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("source", src);
        summary.put("destination", dest);
        summary.put("days", days);
        summary.put("travellers", req.getTravellers());
        summary.put("budget", req.getBudget());
        summary.put("accommodation", req.getAccommodation());
        summary.put("suggestedTransport", transport);
        summary.put("transportReason", transportReason);
        summary.put("isAI", false);

        return Map.of("summary", summary, "itinerary", itinerary);
    }

    private String buildItineraryPrompt(String src, String dest, int days, ItineraryRequest req,
                                         Map<String, Object> pkg, List<String> pkgCities) {
        String pkgInfo = pkg != null ? " This is based on the \"" + pkg.get("name") + "\" package which includes: " + String.join(", ", pkgCities) : "";
        return "Generate a highly detailed Indian travel itinerary from " + src + " to " + dest + ".\n" +
            "Duration: " + days + " days, Budget: ₹" + req.getBudget() + " for " + req.getTravellers() + " person(s)\n" +
            "Accommodation: " + req.getAccommodation() + pkgInfo + "\n" +
            "Return ONLY JSON: { \"suggestedTransport\": \"Flight or Train\", \"transportReason\": \"...\", " +
            "\"itinerary\": [{ \"day\": 1, \"title\": \"Day 1: ...\", \"events\": [{ \"time\": \"08:30 AM\", \"activity\": \"...\" }] }] }";
    }
}

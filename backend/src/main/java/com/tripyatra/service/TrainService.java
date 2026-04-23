package com.tripyatra.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Train Service — handles train search via Indian Rail API and AI fallback.
 * Demonstrates: Service Layer, External API integration, RestTemplate, Strategy pattern
 */
@Service
public class TrainService {

    private static final Logger log = LoggerFactory.getLogger(TrainService.class);

    @Value("${rail.api.key:}")
    private String railApiKey;

    @Value("${rail.api.base-url:http://indianrailapi.com/api/v2}")
    private String railBaseUrl;

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    private final StationDataService stationDataService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public TrainService(StationDataService stationDataService) {
        this.stationDataService = stationDataService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Search for trains between two stations.
     * Strategy: 1) Live Rail API → 2) AI Generation → 3) Demo Fallback
     */
    public Map<String, Object> searchTrains(String source, String destination, String date, int travellers) {
        log.info("=== Train Search: {} -> {} | {} ===", source, destination, date);

        String srcCode = stationDataService.getStationCode(source);
        String dstCode = stationDataService.getStationCode(destination);
        log.info("Station codes: {} -> {}", srcCode, dstCode);

        // Strategy 1: Try Indian Rail API
        if (railApiKey != null && !railApiKey.isEmpty()) {
            try {
                List<Map<String, Object>> trains = searchViaRailAPI(source, destination, srcCode, dstCode);
                if (trains != null && !trains.isEmpty()) {
                    log.info("✅ RailAPI returned {} trains", trains.size());
                    return buildResponse(trains, source, destination, date, travellers, "indianrailapi", srcCode, dstCode);
                }
            } catch (Exception e) {
                log.warn("RailAPI error: {}", e.getMessage());
            }
        }

        // Strategy 2: Try AI (OpenAI → Gemini)
        List<Map<String, Object>> aiTrains = searchViaAI(source, destination, date, srcCode, dstCode);
        if (aiTrains != null && !aiTrains.isEmpty()) {
            return buildResponse(aiTrains, source, destination, date, travellers, "ai", srcCode, dstCode);
        }

        // Strategy 3: Demo fallback
        log.info("Using demo fallback");
        List<Map<String, Object>> fallback = generateFallbackTrains(source, destination, srcCode, dstCode);
        return buildResponse(fallback, source, destination, date, travellers, "demo", srcCode, dstCode);
    }

    /** Call Indian Rail API for live train data */
    private List<Map<String, Object>> searchViaRailAPI(String source, String dest, String srcCode, String dstCode) {
        String url = String.format("%s/TrainBetweenStation/apikey/%s/From/%s/To/%s",
                railBaseUrl, railApiKey, srcCode, dstCode);
        log.info("RailAPI call: {}", url.replaceAll(railApiKey, "***"));

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        JsonNode body = response.getBody();

        if (body != null && "200".equals(body.path("ResponseCode").asText())) {
            JsonNode trainsNode = body.path("Trains");
            if (trainsNode.isArray() && trainsNode.size() > 0) {
                return transformRailApiTrains(trainsNode, source, dest, srcCode, dstCode);
            }
        }
        return null;
    }

    /** Transform Rail API response to our format */
    private List<Map<String, Object>> transformRailApiTrains(JsonNode apiTrains, String src, String dst, String srcCode, String dstCode) {
        List<Map<String, Object>> trains = new ArrayList<>();
        double[] srcCoords = stationDataService.getCoordinates(src);
        double[] dstCoords = stationDataService.getCoordinates(dst);
        Random random = new Random();

        for (JsonNode t : apiTrains) {
            String trainName = t.path("TrainName").asText("");
            String trainType = determineTrainType(trainName, t.path("TrainType").asText(""));

            Map<String, Object> train = new LinkedHashMap<>();
            train.put("train_number", t.path("TrainNo").asText(t.path("TrainNumber").asText("")));
            train.put("train_name", toTitleCase(trainName));
            train.put("train_type", trainType);
            train.put("source", src);
            train.put("source_code", t.path("Source").asText(srcCode));
            train.put("destination", dst);
            train.put("destination_code", t.path("Destination").asText(dstCode));
            train.put("departure", t.path("DepartureTime").asText("--:--"));
            train.put("arrival", t.path("ArrivalTime").asText("--:--"));
            train.put("duration", formatDuration(t.path("TravelTime").asText("")));
            train.put("runs_on", List.of("Mon","Tue","Wed","Thu","Fri","Sat","Sun"));
            train.put("classes", generateClasses(trainType, random));
            train.put("pantry", List.of("Rajdhani","Duronto","Shatabdi").contains(trainType));
            train.put("distance_km", null);
            train.put("source_coords", srcCoords.length == 2 ? srcCoords[0]+","+srcCoords[1] : "");
            train.put("dest_coords", dstCoords.length == 2 ? dstCoords[0]+","+dstCoords[1] : "");
            trains.add(train);
        }
        return trains;
    }

    /** Call OpenAI or Gemini for AI-generated train data */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> searchViaAI(String source, String dest, String date, String srcCode, String dstCode) {
        String prompt = buildTrainSearchPrompt(source, dest, date);
        double[] srcCoords = stationDataService.getCoordinates(source);
        double[] dstCoords = stationDataService.getCoordinates(dest);

        // Try OpenAI first
        if (openaiApiKey != null && !openaiApiKey.isEmpty()) {
            try {
                log.info("[AI] Trying OpenAI for train search");
                String jsonResponse = callOpenAI(prompt);
                Map<String, Object> parsed = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
                List<Map<String, Object>> trains = (List<Map<String, Object>>) parsed.get("trains");
                if (trains != null && !trains.isEmpty()) {
                    enrichAITrains(trains, source, dest, srcCode, dstCode, srcCoords, dstCoords);
                    log.info("✅ OpenAI returned {} trains", trains.size());
                    return trains;
                }
            } catch (Exception e) {
                log.warn("[AI] OpenAI failed: {}", e.getMessage());
            }
        }

        // Try Gemini
        if (geminiApiKey != null && !geminiApiKey.isEmpty()) {
            String[] models = {"gemini-2.5-flash", "gemini-2.0-flash", "gemini-flash-latest"};
            for (String model : models) {
                try {
                    log.info("[AI] Trying Gemini model: {}", model);
                    String jsonResponse = callGemini(prompt, model);
                    Map<String, Object> parsed = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
                    List<Map<String, Object>> trains = (List<Map<String, Object>>) parsed.get("trains");
                    if (trains != null && !trains.isEmpty()) {
                        enrichAITrains(trains, source, dest, srcCode, dstCode, srcCoords, dstCoords);
                        log.info("✅ Gemini ({}) returned {} trains", model, trains.size());
                        return trains;
                    }
                } catch (Exception e) {
                    log.warn("[AI] Gemini {} failed: {}", model, e.getMessage());
                }
            }
        }

        return null;
    }

    /** Call OpenAI Chat Completions API via REST */
    private String callOpenAI(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> body = Map.of(
            "model", "gpt-4o-mini",
            "messages", List.of(
                Map.of("role", "system", "content", "You are a professional Indian Railway API simulator that strictly outputs JSON data matching the requested schema."),
                Map.of("role", "user", "content", prompt)
            ),
            "response_format", Map.of("type", "json_object")
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, request, JsonNode.class);

        return response.getBody().path("choices").get(0).path("message").path("content").asText();
    }

    /** Call Google Gemini API via REST */
    private String callGemini(String prompt, String model) {
        String url = String.format(
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
            model, geminiApiKey
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", prompt + "\nIMPORTANT: Return ONLY valid JSON, no markdown formatting.")
                ))
            )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, request, JsonNode.class);

        String text = response.getBody()
                .path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();

        // Clean markdown code fences if present
        return text.replaceAll("```json", "").replaceAll("```", "").trim();
    }

    /** Enrich AI-generated trains with station codes, coordinates, and availability */
    private void enrichAITrains(List<Map<String, Object>> trains, String source, String dest,
                               String srcCode, String dstCode, double[] srcCoords, double[] dstCoords) {
        Random random = new Random();
        for (int i = 0; i < trains.size(); i++) {
            Map<String, Object> t = trains.get(i);
            t.put("source", source);
            t.put("source_code", srcCode);
            t.put("destination", dest);
            t.put("destination_code", dstCode);
            t.put("source_coords", srcCoords.length == 2 ? srcCoords[0]+","+srcCoords[1] : "");
            t.put("dest_coords", dstCoords.length == 2 ? dstCoords[0]+","+dstCoords[1] : "");

            if (t.get("train_number") == null) t.put("train_number", "120" + i);
            if (t.get("train_name") == null) t.put("train_name", "Express");
            if (t.get("departure") == null) t.put("departure", "--:--");
            if (t.get("arrival") == null) t.put("arrival", "--:--");

            // Add random availability to classes
            Object classesObj = t.get("classes");
            if (classesObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> classes = (List<Map<String, Object>>) classesObj;
                for (Map<String, Object> cls : classes) {
                    cls.put("available", random.nextInt(160) > 40 ? random.nextInt(180) + 10 : 0);
                }
            }
        }
    }

    /** Generate demo/fallback train data */
    private List<Map<String, Object>> generateFallbackTrains(String source, String dest, String srcCode, String dstCode) {
        List<String> days = List.of("Mon","Tue","Wed","Thu","Fri","Sat","Sun");
        Random random = new Random();

        Map<String, Object> train1 = new LinkedHashMap<>();
        train1.put("train_number", "12001");
        train1.put("train_name", source + " " + dest + " Superfast");
        train1.put("train_type", "Superfast");
        train1.put("source", source);
        train1.put("source_code", srcCode);
        train1.put("destination", dest);
        train1.put("destination_code", dstCode);
        train1.put("departure", "06:30");
        train1.put("arrival", "14:00");
        train1.put("duration", "7h 30m");
        train1.put("runs_on", days);
        train1.put("classes", List.of(
            Map.of("name","2A","fare",1680,"available",24),
            Map.of("name","3A","fare",1148,"available",72),
            Map.of("name","SL","fare",415,"available",180)
        ));
        train1.put("pantry", true);
        train1.put("distance_km", 600);

        Map<String, Object> train2 = new LinkedHashMap<>();
        train2.put("train_number", "12002");
        train2.put("train_name", source + " Rajdhani Express");
        train2.put("train_type", "Rajdhani");
        train2.put("source", source);
        train2.put("source_code", srcCode);
        train2.put("destination", dest);
        train2.put("destination_code", dstCode);
        train2.put("departure", "17:00");
        train2.put("arrival", "07:00");
        train2.put("duration", "14h 00m");
        train2.put("runs_on", days);
        train2.put("classes", List.of(
            Map.of("name","1A","fare",3960,"available",6),
            Map.of("name","2A","fare",2340,"available",28),
            Map.of("name","3A","fare",1608,"available",0)
        ));
        train2.put("pantry", true);
        train2.put("distance_km", 600);

        return List.of(train1, train2);
    }

    // ── Helper Methods ──────────────────────────────────

    private String determineTrainType(String trainName, String typeCode) {
        String upper = trainName.toUpperCase();
        if ("RAJ".equals(typeCode) || upper.contains("RAJDHANI")) return "Rajdhani";
        if ("SHTBDI".equals(typeCode) || upper.contains("SHATABDI")) return "Shatabdi";
        if ("SF".equals(typeCode) || upper.contains("SUPERFAST")) return "Superfast";
        if ("MAL".equals(typeCode) || upper.contains("MAIL")) return "Mail";
        if ("DRT".equals(typeCode) || upper.contains("DURONTO")) return "Duronto";
        if ("JAN".equals(typeCode) || upper.contains("JAN SHATABDI")) return "Jan Shatabdi";
        if ("GR".equals(typeCode) || upper.contains("GARIB RATH")) return "Garib Rath";
        return "Express";
    }

    private List<Map<String, Object>> generateClasses(String trainType, Random random) {
        Map<String, List<Map<String, Object>>> fareMap = new LinkedHashMap<>();
        fareMap.put("Rajdhani", List.of(Map.of("name","1A","fare",4200), Map.of("name","2A","fare",2500), Map.of("name","3A","fare",1720)));
        fareMap.put("Duronto", List.of(Map.of("name","1A","fare",3800), Map.of("name","2A","fare",2240), Map.of("name","3A","fare",1540)));
        fareMap.put("Shatabdi", List.of(Map.of("name","EC","fare",1600), Map.of("name","CC","fare",720)));
        fareMap.put("Jan Shatabdi", List.of(Map.of("name","CC","fare",650), Map.of("name","2S","fare",240)));
        fareMap.put("Superfast", List.of(Map.of("name","2A","fare",1800), Map.of("name","3A","fare",1230), Map.of("name","SL","fare",445)));
        fareMap.put("Mail", List.of(Map.of("name","2A","fare",1680), Map.of("name","3A","fare",1148), Map.of("name","SL","fare",415)));
        fareMap.put("Garib Rath", List.of(Map.of("name","3A","fare",1050)));
        fareMap.put("Express", List.of(Map.of("name","2A","fare",1540), Map.of("name","3A","fare",1052), Map.of("name","SL","fare",380)));

        List<Map<String, Object>> baseClasses = fareMap.getOrDefault(trainType, fareMap.get("Express"));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> cls : baseClasses) {
            Map<String, Object> enriched = new LinkedHashMap<>(cls);
            enriched.put("available", random.nextInt(160) > 40 ? random.nextInt(180) + 10 : 0);
            result.add(enriched);
        }
        return result;
    }

    private String formatDuration(String travelTime) {
        if (travelTime == null || travelTime.isEmpty()) return "";
        String clean = travelTime.replace("H", "").trim();
        if (clean.contains(":")) {
            String[] parts = clean.split(":");
            return Integer.parseInt(parts[0]) + "h " + parts[1] + "m";
        }
        return clean;
    }

    private String toTitleCase(String str) {
        if (str == null || str.isEmpty()) return str;
        StringBuilder result = new StringBuilder();
        boolean nextUpper = true;
        for (char c : str.toLowerCase().toCharArray()) {
            if (Character.isWhitespace(c) || c == '-') {
                nextUpper = true;
                result.append(c);
            } else if (nextUpper) {
                result.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private String buildTrainSearchPrompt(String source, String dest, String date) {
        return "Generate a realistic Indian train schedule between " + source + " and " + dest +
               " for the date " + date + ".\nProvide 4-5 train options. Return ONLY a JSON object with this exact structure:\n" +
               "{ \"trains\": [{ \"train_number\": \"12951\", \"train_name\": \"Mumbai Rajdhani\", " +
               "\"train_type\": \"Express, Rajdhani, Shatabdi, Superfast, Mail, Duronto, or Jan Shatabdi\", " +
               "\"departure\": \"HH:MM\", \"arrival\": \"HH:MM\", \"duration\": \"14h 55m\", " +
               "\"runs_on\": [\"Mon\",\"Tue\",\"Wed\",\"Thu\",\"Fri\",\"Sat\",\"Sun\"], " +
               "\"pantry\": true, \"distance_km\": 1384, " +
               "\"classes\": [{ \"name\": \"1A, 2A, 3A, SL, CC, etc\", \"fare\": 1500 }] }] }\n" +
               "Return only the valid JSON, no markdown formatting or extra text.";
    }

    private Map<String, Object> buildResponse(List<Map<String, Object>> trains, String source,
                                               String dest, String date, int travellers,
                                               String dataSource, String srcCode, String dstCode) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("trains", trains);
        response.put("source", source);
        response.put("destination", dest);
        response.put("date", date);
        response.put("travellers", travellers);
        response.put("dataSource", dataSource);
        response.put("srcCode", srcCode);
        response.put("dstCode", dstCode);
        return response;
    }
}

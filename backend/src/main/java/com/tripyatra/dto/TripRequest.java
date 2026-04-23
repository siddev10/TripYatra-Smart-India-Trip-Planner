package com.tripyatra.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DTO for creating/saving a trip.
 * itinerary and summary accept any JSON type (object, array, or string)
 * and are serialized to String for database storage.
 */
public class TripRequest {

    private static final ObjectMapper mapper = new ObjectMapper();

    private String title;
    private String source;
    private String destination;
    private String date;
    private Integer travellers;
    private String budget;
    private String notes;
    private Object itinerary;  // Accepts JSON array/object from frontend
    private Object summary;    // Accepts JSON object from frontend

    public TripRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Integer getTravellers() { return travellers; }
    public void setTravellers(Integer travellers) { this.travellers = travellers; }

    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Object getItinerary() { return itinerary; }
    public void setItinerary(Object itinerary) { this.itinerary = itinerary; }

    public Object getSummary() { return summary; }
    public void setSummary(Object summary) { this.summary = summary; }

    /** Convert itinerary object to JSON string for DB storage */
    public String getItineraryAsString() {
        return toJsonString(itinerary);
    }

    /** Convert summary object to JSON string for DB storage */
    public String getSummaryAsString() {
        return toJsonString(summary);
    }

    private String toJsonString(Object obj) {
        if (obj == null) return null;
        if (obj instanceof String) return (String) obj;
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return obj.toString();
        }
    }
}

package com.tripyatra.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Trip entity — maps to the 'trips' table in the database.
 * Demonstrates: JPA Entity, Column Mapping, JSON serialization control
 */
@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    @JsonProperty("userId")
    private Long userId;

    @Column(length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String source;

    @Column(nullable = false, length = 100)
    private String destination;

    @Column(nullable = false, length = 20)
    private String date;

    @Column
    private Integer travellers;

    @Column(length = 50)
    private String budget;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String itinerary; // Stored as JSON string

    @Column(columnDefinition = "TEXT")
    private String summary; // Stored as JSON string

    @Column(length = 20)
    private String status;

    @Column(name = "created_at")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Column(name = "source_coords", length = 50)
    @JsonProperty("source_coords")
    private String sourceCoords;

    @Column(name = "dest_coords", length = 50)
    @JsonProperty("dest_coords")
    private String destCoords;

    // ── Constructors ────────────────────────────────────

    /** Default constructor required by JPA/Hibernate */
    public Trip() {
    }

    /** Full constructor for creating trips */
    public Trip(Long userId, String title, String source, String destination,
                String date, Integer travellers, String budget, String notes) {
        this.userId = userId;
        this.title = (title != null) ? title : source + " to " + destination;
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.travellers = (travellers != null) ? travellers : 1;
        this.budget = (budget != null) ? budget : "";
        this.notes = (notes != null) ? notes : "";
        this.status = "planned";
        this.createdAt = LocalDateTime.now();
    }

    // ── Getters & Setters ───────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getTravellers() {
        return travellers;
    }

    public void setTravellers(Integer travellers) {
        this.travellers = travellers;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getItinerary() {
        return itinerary;
    }

    public void setItinerary(String itinerary) {
        this.itinerary = itinerary;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSourceCoords() {
        return sourceCoords;
    }

    public void setSourceCoords(String sourceCoords) {
        this.sourceCoords = sourceCoords;
    }

    public String getDestCoords() {
        return destCoords;
    }

    public void setDestCoords(String destCoords) {
        this.destCoords = destCoords;
    }

    @Override
    public String toString() {
        return "Trip{id=" + id + ", title='" + title + "', " + source + " → " + destination + "}";
    }
}

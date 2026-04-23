package com.tripyatra.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for train search requests.
 */
public class TrainSearchRequest {

    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotBlank(message = "Date is required")
    private String date;

    private int travellers = 1;

    public TrainSearchRequest() {}

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getTravellers() { return travellers; }
    public void setTravellers(int travellers) { this.travellers = travellers; }
}

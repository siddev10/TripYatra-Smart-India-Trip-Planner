package com.tripyatra.dto;

/**
 * DTO for itinerary generation requests.
 */
public class ItineraryRequest {

    private Integer packageId;
    private String budget;
    private Integer travellers;
    private String accommodation;
    private Integer days;
    private String source;
    private String destination;

    public ItineraryRequest() {}

    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }

    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }

    public Integer getTravellers() { return travellers; }
    public void setTravellers(Integer travellers) { this.travellers = travellers; }

    public String getAccommodation() { return accommodation; }
    public void setAccommodation(String accommodation) { this.accommodation = accommodation; }

    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
}

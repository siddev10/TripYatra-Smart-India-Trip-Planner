package com.tripyatra.service;

import com.tripyatra.dto.TripRequest;
import com.tripyatra.model.Trip;
import com.tripyatra.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Trip Service — handles CRUD operations for user trips.
 * Demonstrates: Service Layer, Repository pattern, Business logic separation
 */
@Service
public class TripService {

    private final TripRepository tripRepository;
    private final StationDataService stationDataService;

    @Autowired
    public TripService(TripRepository tripRepository, StationDataService stationDataService) {
        this.tripRepository = tripRepository;
        this.stationDataService = stationDataService;
    }

    /**
     * Create a new trip for the authenticated user.
     */
    public Trip createTrip(Long userId, TripRequest request) {
        Trip trip = new Trip(
                userId,
                request.getTitle(),
                request.getSource(),
                request.getDestination(),
                request.getDate(),
                request.getTravellers(),
                request.getBudget(),
                request.getNotes()
        );

        trip.setItinerary(request.getItineraryAsString());
        trip.setSummary(request.getSummaryAsString());
        trip.setCreatedAt(LocalDateTime.now());

        // Set geographic coordinates for map display
        double[] srcCoords = stationDataService.getCoordinates(request.getSource());
        double[] dstCoords = stationDataService.getCoordinates(request.getDestination());
        if (srcCoords.length == 2) {
            trip.setSourceCoords(srcCoords[0] + "," + srcCoords[1]);
        }
        if (dstCoords.length == 2) {
            trip.setDestCoords(dstCoords[0] + "," + dstCoords[1]);
        }

        return tripRepository.save(trip); // Hibernate INSERT
    }

    /**
     * Get all trips for a specific user, newest first.
     */
    public List<Trip> getUserTrips(Long userId) {
        List<Trip> trips = tripRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // Ensure coordinates are populated even for older trips
        for (Trip trip : trips) {
            if (trip.getSourceCoords() == null || trip.getSourceCoords().isEmpty()) {
                double[] coords = stationDataService.getCoordinates(trip.getSource());
                if (coords.length == 2) {
                    trip.setSourceCoords(coords[0] + "," + coords[1]);
                }
            }
            if (trip.getDestCoords() == null || trip.getDestCoords().isEmpty()) {
                double[] coords = stationDataService.getCoordinates(trip.getDestination());
                if (coords.length == 2) {
                    trip.setDestCoords(coords[0] + "," + coords[1]);
                }
            }
        }

        return trips;
    }

    /**
     * Delete a trip (only if owned by the authenticated user).
     * @return true if the trip was found and deleted
     */
    public boolean deleteTrip(Long tripId, Long userId) {
        Optional<Trip> trip = tripRepository.findByIdAndUserId(tripId, userId);
        if (trip.isPresent()) {
            tripRepository.delete(trip.get()); // Hibernate DELETE
            return true;
        }
        return false;
    }

    /**
     * Count total trips for a user — used for dashboard stats.
     */
    public long countUserTrips(Long userId) {
        return tripRepository.countByUserId(userId);
    }
}

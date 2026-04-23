package com.tripyatra.repository;

import com.tripyatra.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Trip Repository — Spring Data JPA interface.
 * Demonstrates: JPA Repository, Custom queries, Transactional operations
 */
@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    /** Get all trips for a user, ordered by newest first */
    List<Trip> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** Find a specific trip belonging to a user */
    Optional<Trip> findByIdAndUserId(Long id, Long userId);

    /** Delete a trip only if it belongs to the user (security) */
    @Transactional
    void deleteByIdAndUserId(Long id, Long userId);

    /** Count trips for a specific user */
    long countByUserId(Long userId);
}

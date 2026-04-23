package com.tripyatra.repository;

import com.tripyatra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository — Spring Data JPA interface.
 * Hibernate auto-generates the SQL queries from method names.
 * Demonstrates: JPA Repository Pattern, Derived Query Methods
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Find user by email — used for login and duplicate check */
    Optional<User> findByEmail(String email);

    /** Check if email already exists — used during registration */
    boolean existsByEmail(String email);
}

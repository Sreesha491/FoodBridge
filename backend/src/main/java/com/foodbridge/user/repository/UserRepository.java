package com.foodbridge.user.repository;

import com.foodbridge.user.model.User;
import com.foodbridge.user.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /** Find a user by their email address (used for login). */
    Optional<User> findByEmail(String email);

    /** Check if an email is already registered. */
    boolean existsByEmail(String email);

    /** Find all users with a specific role. */
    List<User> findByRole(UserRole role);

    /** Find all active users. */
    List<User> findByActiveTrue();
}

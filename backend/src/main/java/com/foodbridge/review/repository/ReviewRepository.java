package com.foodbridge.review.repository;

import com.foodbridge.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Review} entities.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {

    /** All reviews left by a specific reviewer. */
    List<Review> findByReviewerId(String reviewerId);

    /** All reviews for a specific target entity. */
    List<Review> findByTargetId(String targetId);

    /** Reviews for a specific target of a given type. */
    List<Review> findByTargetIdAndTargetType(String targetId, String targetType);

    /** Check if a reviewer has already reviewed a target. */
    boolean existsByReviewerIdAndTargetId(String reviewerId, String targetId);
}

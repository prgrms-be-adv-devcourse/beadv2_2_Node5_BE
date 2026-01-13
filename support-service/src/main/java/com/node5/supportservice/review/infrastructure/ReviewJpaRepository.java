package com.node5.supportservice.review.infrastructure;

import com.node5.supportservice.review.domain.ReviewStatic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ReviewJpaRepository extends JpaRepository<ReviewStatic, UUID> {
    ReviewStatic findByProductId(UUID productId);

    @Modifying
    @Query("UPDATE ReviewStatic r SET " +
            "r.reviewCount = r.reviewCount + 1, " +
            "r.ratingCount1 = r.ratingCount1 + CASE WHEN :rating = 1 THEN 1 ELSE 0 END, " +
            "r.ratingCount2 = r.ratingCount2 + CASE WHEN :rating = 2 THEN 1 ELSE 0 END, " +
            "r.ratingCount3 = r.ratingCount3 + CASE WHEN :rating = 3 THEN 1 ELSE 0 END, " +
            "r.ratingCount4 = r.ratingCount4 + CASE WHEN :rating = 4 THEN 1 ELSE 0 END, " +
            "r.ratingCount5 = r.ratingCount5 + CASE WHEN :rating = 5 THEN 1 ELSE 0 END " +
            "WHERE r.productId = :productId")
    void incrementStatistics(@Param("productId") UUID productId, @Param("rating") Integer rating);

    @Modifying
    @Query("UPDATE ReviewStatic r SET " +
            "r.reviewCount = CASE WHEN r.reviewCount > 0 THEN r.reviewCount - 1 ELSE 0 END, " +
            "r.ratingCount1 = CASE WHEN :rating = 1 AND r.ratingCount1 > 0 THEN r.ratingCount1 - 1 ELSE r.ratingCount1 END, " +
            "r.ratingCount2 = CASE WHEN :rating = 2 AND r.ratingCount2 > 0 THEN r.ratingCount2 - 1 ELSE r.ratingCount2 END, " +
            "r.ratingCount3 = CASE WHEN :rating = 3 AND r.ratingCount3 > 0 THEN r.ratingCount3 - 1 ELSE r.ratingCount3 END, " +
            "r.ratingCount4 = CASE WHEN :rating = 4 AND r.ratingCount4 > 0 THEN r.ratingCount4 - 1 ELSE r.ratingCount4 END, " +
            "r.ratingCount5 = CASE WHEN :rating = 5 AND r.ratingCount5 > 0 THEN r.ratingCount5 - 1 ELSE r.ratingCount5 END " +
            "WHERE r.productId = :productId")
    void decrementStatistics(@Param("productId") UUID productId, @Param("rating") Integer rating);

    @Modifying
    @Query("UPDATE ReviewStatic r SET " +
            "r.ratingCount1 = r.ratingCount1 + CASE WHEN :newRating = 1 THEN 1 ELSE 0 END - CASE WHEN :oldRating = 1 THEN 1 ELSE 0 END, " +
            "r.ratingCount2 = r.ratingCount2 + CASE WHEN :newRating = 2 THEN 1 ELSE 0 END - CASE WHEN :oldRating = 2 THEN 1 ELSE 0 END, " +
            "r.ratingCount3 = r.ratingCount3 + CASE WHEN :newRating = 3 THEN 1 ELSE 0 END - CASE WHEN :oldRating = 3 THEN 1 ELSE 0 END, " +
            "r.ratingCount4 = r.ratingCount4 + CASE WHEN :newRating = 4 THEN 1 ELSE 0 END - CASE WHEN :oldRating = 4 THEN 1 ELSE 0 END, " +
            "r.ratingCount5 = r.ratingCount5 + CASE WHEN :newRating = 5 THEN 1 ELSE 0 END - CASE WHEN :oldRating = 5 THEN 1 ELSE 0 END " +
            "WHERE r.productId = :productId")
    void updateStatisticsOnReviewEdit(UUID productId, Integer oldRating, Integer newRating);
    Boolean existsByProductId(UUID productId);
}

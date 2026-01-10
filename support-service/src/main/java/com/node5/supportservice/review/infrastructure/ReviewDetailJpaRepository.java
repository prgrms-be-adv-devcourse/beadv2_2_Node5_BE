package com.node5.supportservice.review.infrastructure;

import com.node5.supportservice.review.domain.ReviewDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ReviewDetailJpaRepository extends JpaRepository<ReviewDetail, UUID> {
    Page<ReviewDetail> findByProductIdOrderByCreatedAtDesc(UUID productId, Pageable pageable);
    Page<ReviewDetail> findByMemberIdOrderByCreatedAtDesc(UUID memberId, Pageable pageable);
    Page<ReviewDetail> findByMemberIdAndProductIdOrderByCreatedAtDesc(UUID memberId, UUID productId, Pageable pageable);
    void deleteById(UUID reviewId);

    @Modifying
    @Query("UPDATE ReviewDetail r SET r.likeCount = r.likeCount + 1 " +
            "WHERE r.id = :reviewId")
    int incrementLikeCount(@Param("reviewId") UUID reviewId);

    Boolean existsByProductIdAndMemberId(UUID productId, UUID memberId);
}

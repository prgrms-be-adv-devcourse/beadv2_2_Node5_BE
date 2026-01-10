package com.node5.supportservice.review.domain;

import com.node5.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "review_like", schema = "support")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewLike extends BaseEntity {

    @Id
    private UUID id;

    @Column(name = "member_id", nullable = false, columnDefinition = "uuid")
    private UUID memberId;

    @Column(name = "review_id", nullable = false, columnDefinition = "uuid")
    private UUID reviewId;

    @Builder
    public ReviewLike(UUID memberId, UUID reviewId) {
        this.id = UUID.randomUUID();
        this.memberId = memberId;
        this.reviewId = reviewId;
    }
}

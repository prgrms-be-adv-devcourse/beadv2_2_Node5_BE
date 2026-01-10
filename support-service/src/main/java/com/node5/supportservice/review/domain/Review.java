package com.node5.supportservice.review.domain;

import com.node5.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Entity
@Table(name = "review", schema = "support",
        uniqueConstraints = {@UniqueConstraint(name = "uk_review_product", columnNames = "product_id")})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "product_id", nullable = false, columnDefinition = "uuid")
    private UUID productId;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "rating_count_1", nullable = false)
    private int ratingCount1;

    @Column(name = "rating_count_2", nullable = false)
    private int ratingCount2;

    @Column(name = "rating_count_3", nullable = false)
    private int ratingCount3;

    @Column(name = "rating_count_4", nullable = false)
    private int ratingCount4;

    @Column(name = "rating_count_5", nullable = false)
    private int ratingCount5;

    @Builder
    public Review(UUID productId) {
        this.id = UUID.randomUUID(); // ID 자동 생성 예시
        this.productId = productId;
        this.reviewCount = 0;
        this.ratingCount1 = 0;
        this.ratingCount2 = 0;
        this.ratingCount3 = 0;
        this.ratingCount4 = 0;
        this.ratingCount5 = 0;
    }

    public BigDecimal calculateAverageRating() {
        if (reviewCount == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        int totalRating = ratingCount1 +
                          ratingCount2 * 2 +
                          ratingCount3 * 3 +
                          ratingCount4 * 4 +
                          ratingCount5 * 5;
        return BigDecimal.valueOf(totalRating)
                .divide(BigDecimal.valueOf(reviewCount), 2, RoundingMode.HALF_UP);
    }
}

package com.node5.supportservice.reviewsummary.domain;

import com.node5.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"review_summary\"", schema = "support")
public class ReviewSummary extends BaseEntity {

    @Id
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "summary", columnDefinition = "text", nullable = false)
    private String summary;

    @Column(name = "summary_end_date", nullable = false)
    private LocalDate summaryEndDate;

    private ReviewSummary(UUID productId, String summary, LocalDate summaryEndDate) {
        this.id = UUID.randomUUID();
        this.productId = productId;
        this.summary = summary;
        this.summaryEndDate = summaryEndDate;
    }

    public static ReviewSummary create(UUID productId, String summary, LocalDate summaryEndDate) {
        return new ReviewSummary(
                productId,
                summary,
                summaryEndDate
        );
    }

    public void update(String summary, LocalDate summaryEndDate) {
        this.summary = summary;
        this.summaryEndDate = summaryEndDate;
    }
}

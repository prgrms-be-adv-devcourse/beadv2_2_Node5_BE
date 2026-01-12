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

    @Column(name = "summarized_review_count", nullable = false)
    private Integer summarizedReviewCount;

    @Column(name = "pros_summary", columnDefinition = "text", nullable = false)
    private String prosSummary;

    @Column(name = "cons_summary", columnDefinition = "text", nullable = false)
    private String consSummary;

    @Column(name = "summary_start_date", nullable = false)
    private LocalDate summaryStartDate;

    @Column(name = "summary_end_date", nullable = false)
    private LocalDate summaryEndDate;

}

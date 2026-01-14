package com.node5.supportservice.review.domain;

import com.node5.common.domain.BaseEntity;
import com.node5.supportservice.global.util.VectorConverter;
import com.node5.supportservice.review.application.dto.ReviewUpdateCommand;
import com.node5.supportservice.review.exception.ReviewErrorCode;
import com.node5.supportservice.review.exception.ReviewException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "review_detail", schema = "support",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_review_detail_product_member",
                        columnNames = {"product_id", "member_id"}
                )
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewDetail extends BaseEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "product_id", nullable = false, columnDefinition = "uuid")
    private UUID productId;

    @Column(name = "member_id", nullable = false, columnDefinition = "uuid")
    private UUID memberId;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "order_id", nullable = false, columnDefinition = "uuid")
    private UUID orderId;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "body", columnDefinition = "text")
    private String body;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Convert(converter = VectorConverter.class)
    @JdbcTypeCode(SqlTypes.OTHER)
    @Column(name = "embedding", columnDefinition = "vector(1536)", nullable = false)
    private float[] embedding;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public ReviewDetail(UUID productId, UUID memberId, String nickname, UUID orderId, int rating, String body) {
        this.id = UUID.randomUUID();
        this.productId = productId;
        this.memberId = memberId;
        this.nickname = nickname;
        this.orderId = orderId;
        this.rating = rating;
        this.body = body;
        this.likeCount = 0;
    }

    public void update(ReviewUpdateCommand command) {
        this.rating = command.rating();
        this.body = command.body();
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void validateMember(UUID memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_UNAUTHORIZED);
        }
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void validateNotDeleted() {
        if (this.deletedAt != null) {
            throw new ReviewException(ReviewErrorCode.REVIEW_DELETED);
        }
    }

    public void validateSelfLike(UUID memberId) {
        if (this.memberId.equals(memberId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_CANNOT_LIKE_OWN);
        }
    }

<<<<<<< HEAD
    public void updateEmbedding(float[] vector) {
        this.embedding = vector;
    }

}
=======
}
>>>>>>> a992d8a (feat: 리뷰 요약 ai 수정)

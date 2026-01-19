package com.node5.supportservice.review.infrastructure;

import com.node5.supportservice.review.domain.ReviewDetail;
import com.pgvector.PGvector;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface ReviewDetailJpaRepository extends JpaRepository<ReviewDetail, UUID> {
    // 상품별 리뷰 조회 (최신순, 삭제된 리뷰 제외)
    Page<ReviewDetail> findByProductIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID productId, Pageable pageable);

    // 회원별 리뷰 조회 (최신순, 삭제된 리뷰 제외)
    Page<ReviewDetail> findByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID memberId, Pageable pageable);

    // 상품별 리뷰 조회 (추천순, 삭제된 리뷰 제외)
    @Query(value = """
    SELECT * FROM support.review_detail 
    WHERE product_id = :productId 
      AND deleted_at IS NULL
    ORDER BY (like_count * EXP(-0.001 * (CURRENT_DATE - created_at::date))) DESC
    """,
    countQuery = """
    SELECT count(*) FROM support.review_detail
    WHERE product_id = :productId AND deleted_at IS NULL
    """,
    nativeQuery = true)
    Page<ReviewDetail> findRecommendedReviews(@Param("productId") UUID productId, Pageable pageable);

    // 리뷰 공감 수 증가
    @Modifying
    @Query("UPDATE ReviewDetail r SET r.likeCount = r.likeCount + 1 " +
            "WHERE r.id = :reviewId")
    void incrementLikeCount(@Param("reviewId") UUID reviewId);

    // 특정 회원이 특정 상품에 리뷰를 작성했는지 여부 확인
    Boolean existsByProductIdAndMemberId(UUID productId, UUID memberId);

    // 전체 기간 특정 상품 리뷰 개수 조회 (삭제된 리뷰 제외)
    long countByProductIdAndDeletedAtIsNull(UUID productId);

    // 특정 시점 이후의 특정 상품 리뷰 개수 조회 (삭제된 리뷰 제외)
    long countByProductIdAndCreatedAtBetweenAndDeletedAtIsNull(UUID productId, LocalDateTime startDate, LocalDateTime endDate);

    // 특정 상품에서 전체 기간 동안 공감 수가 가장 많은 리뷰 ID 조회 (삭제된 리뷰 제외)
    Optional<ReviewDetail> findTopByProductIdAndDeletedAtIsNullOrderByLikeCountDesc(UUID productId);

    //특정 상품에서 최근 1달 동안 공감 수가 가장 많은 리뷰 ID 조회 (삭제된 리뷰 제외)
    Optional<ReviewDetail> findTopByProductIdAndCreatedAtBetweenAndDeletedAtIsNullOrderByLikeCountDesc(UUID productId, LocalDateTime startDate, LocalDateTime endDate);

    // 유사한 리뷰 조회 (임베딩 벡터 기반)
    @Query(value = """
    SELECT * FROM support.review_detail 
    WHERE product_id = :productId 
      AND id != :reviewId 
      AND deleted_at IS NULL
      AND (embedding OPERATOR(public.<=>) (SELECT embedding FROM support.review_detail WHERE id = :reviewId)) < 0.4
    ORDER BY embedding OPERATOR(public.<=>) (SELECT embedding FROM support.review_detail WHERE id = :reviewId) ASC 
    LIMIT 4
    """, nativeQuery = true)
    List<ReviewDetail> findSimilarReviews(
            @Param("productId") UUID productId,
            @Param("reviewId") UUID reviewId
    );

    // 임베딩 되지 않은 리뷰 조회 (테스트용)
    List<ReviewDetail> findAllByEmbeddingIsNullOrderByCreatedAtDesc();

    // 리뷰 존재 여부 확인 (회원, 주문, 상품 기준, 삭제된 리뷰 제외)
    Boolean existsByMemberIdAndOrderIdAndProductIdAndDeletedAtIsNull(UUID memberId, UUID orderId, UUID productId);

    // 기존 인덱싱 제거
    @Modifying
    @Query(value = "DROP INDEX IF EXISTS support.idx_review_detail_embedding", nativeQuery = true)
    void dropEmbeddingIndex();

    // 인덱싱 생성
    @Modifying
    @Query(value = "CREATE INDEX IF NOT EXISTS idx_review_detail_embedding " +
            "ON support.review_detail USING ivfflat (embedding vector_cosine_ops) " +
            "WITH (lists = 100) WHERE deleted_at IS NULL", nativeQuery = true)
    void createEmbeddingIndex();

    // 테이블 분석
    @Modifying
    @Query(value = "ANALYZE support.review_detail", nativeQuery = true)
    void analyzeTable();

//    // 리뷰 임베딩 업데이트
//    @Modifying
//    @Transactional
//    @Query(value = "UPDATE support.review_detail SET embedding = :vector WHERE id = :reviewId", nativeQuery = true)
//    void updateReviewEmbedding(@Param("reviewId") UUID reviewId, @Param("vector") PGvector vector);

}

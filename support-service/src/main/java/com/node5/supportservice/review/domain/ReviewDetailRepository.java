package com.node5.supportservice.review.domain;

import com.pgvector.PGvector;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface ReviewDetailRepository {
    // 상품별 리뷰 조회 (최신순, 삭제된 리뷰 제외)
    Page<ReviewDetail> findAllLatestByProduct(UUID productId, Pageable pageable);

    // 회원별 리뷰 조회 (최신순, 삭제된 리뷰 제외)
    Page<ReviewDetail> findAllLatestByMember(UUID memberId, Pageable pageable);

    // 리뷰 ID로 리뷰 조회
    Optional<ReviewDetail> findById(UUID id);

    // 리뷰 저장
    void save(ReviewDetail reviewDetail);

    // 리뷰 공감 수 증가
    void incrementLikeCount(UUID reviewId);

    // 특정 회원이 특정 상품에 리뷰를 작성했는지 여부 확인
    Boolean existsReview(UUID productId, UUID memberId);

    // 전체 기간 특정 상품 리뷰 개수 조회
    long countReviews(UUID productId);

    // 특정 기간 사이의 특정 상품 리뷰 개수 조회
    long countReviewsBetween(UUID productId, LocalDateTime startDate, LocalDateTime endDate);

    // 특정 상품에서 전체 기간 동안 공감 수가 가장 많은 리뷰 ID 조회
    Optional<ReviewDetail> findTopReview(UUID productId);

    //특정 상품에서 특정 기간 사이의 공감 수가 가장 많은 리뷰 ID 조회
    Optional<ReviewDetail> findTopReviewBetween(UUID productId, LocalDateTime startDate, LocalDateTime endDate);

    // 유사한 리뷰 조회
    List<ReviewDetail> findSimilarReviews(UUID productId, UUID reviewId);

    // 임베딩되지 않은 리뷰 조회
    List<ReviewDetail> findReviewsWithoutEmbedding();

    // 임베딩 업데이트
    void updateReviewEmbedding(UUID reviewId, String vector);

}

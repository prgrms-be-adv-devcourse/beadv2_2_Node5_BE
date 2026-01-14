package com.node5.supportservice.review.infrastructure;

import com.node5.supportservice.review.domain.ReviewDetail;
import com.node5.supportservice.review.domain.ReviewDetailRepository;
import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewDetailRepositoryAdapter implements ReviewDetailRepository {
    private final ReviewDetailJpaRepository reviewDetailJpaRepository;

    // 상품별 리뷰 조회 (최신순, 삭제된 리뷰 제외)
    @Override
    public Page<ReviewDetail> findAllLatestByProduct(UUID productId, Pageable pageable) {
        return reviewDetailJpaRepository.findByProductIdAndDeletedAtIsNullOrderByCreatedAtDesc(productId, pageable);
    }

    // 회원별 리뷰 조회 (최신순, 삭제된 리뷰 제외)
    @Override
    public Page<ReviewDetail> findAllLatestByMember(UUID memberId, Pageable pageable) {
        return reviewDetailJpaRepository.findByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(memberId, pageable);
    }

    @Override
    public Optional<ReviewDetail> findById(UUID id) {
        return reviewDetailJpaRepository.findById(id);
    }

    @Override
    public void save(ReviewDetail reviewDetail) {
        reviewDetailJpaRepository.save(reviewDetail);
    }

    @Override
    public void incrementLikeCount(UUID reviewId) {
        reviewDetailJpaRepository.incrementLikeCount(reviewId);
    }

    @Override
    public Boolean existsReview(UUID productId, UUID memberId) {
        return reviewDetailJpaRepository.existsByProductIdAndMemberIdAndDeletedAtIsNull(productId, memberId);
    }

    // 전체 기간 특정 상품 리뷰 개수 조회 (삭제된 리뷰 제외)
    @Override
    public long countReviews(UUID productId) {
        return reviewDetailJpaRepository.countByProductIdAndDeletedAtIsNull(productId);
    }

    // 특정 기간 사이의 특정 상품 리뷰 개수 조회 (삭제된 리뷰 제외)
    @Override
    public long countReviewsBetween(UUID productId, LocalDateTime startDate, LocalDateTime endDate) {
        return reviewDetailJpaRepository.countByProductIdAndCreatedAtBetweenAndDeletedAtIsNull(productId, startDate, endDate);
    }

    // 특정 상품에서 전체 기간 동안 공감 수가 가장 많은 리뷰 ID 조회 (삭제된 리뷰 제외)
    @Override
    public Optional<ReviewDetail> findTopReview(UUID productId) {
        return reviewDetailJpaRepository.findTopByProductIdAndDeletedAtIsNullOrderByLikeCountDesc(productId);
    }

    //특정 상품에서 최근 1달 동안 공감 수가 가장 많은 리뷰 ID 조회 (삭제된 리뷰 제외)
    @Override
    public Optional<ReviewDetail> findTopReviewBetween(UUID productId, LocalDateTime startDate, LocalDateTime endDate) {
        return reviewDetailJpaRepository.findTopByProductIdAndCreatedAtBetweenAndDeletedAtIsNullOrderByLikeCountDesc(productId, startDate, endDate);
    }

    // 유사한 리뷰 조회
    @Override
    public List<ReviewDetail> findSimilarReviews(UUID productId, UUID reviewId) {
        return reviewDetailJpaRepository.findSimilarReviews(productId, reviewId);
    }

    // 임베딩되지 않은 리뷰 조회
    @Override
    public List<ReviewDetail> findReviewsWithoutEmbedding() {
        return reviewDetailJpaRepository.findAllByEmbeddingIsNullOrderByCreatedAtDesc();
    }

//    @Override
//    public void updateReviewEmbedding(UUID reviewId, PGvector vector) {
//        reviewDetailJpaRepository.updateReviewEmbedding(reviewId, vector);
//    }
}

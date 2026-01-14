package com.node5.supportservice.review.application;

import com.node5.common.event.ProductDiscontinuedEvent;
import com.node5.common.event.ReviewCreatedEvent;
import com.node5.supportservice.review.application.dto.*;
import com.node5.supportservice.review.client.MemberClient;
import com.node5.supportservice.review.client.ProductClient;
import com.node5.supportservice.review.domain.*;
import com.node5.supportservice.review.exception.ReviewErrorCode;
import com.node5.supportservice.review.exception.ReviewException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewStaticRepository reviewRepository;
    private final ReviewDetailRepository reviewDetailRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    private final ApplicationEventPublisher eventPublisher;
    private final EmbeddingModel embeddingModel;
    private final MemberClient memberClient;
    private final ProductClient productClient;

    // 상품에 리뷰 작성
    @Transactional
    public ReviewIdInfo createReviewDetail(UUID memberId, ReviewCreateCommand command) {

        if (reviewDetailRepository.existsReview(command.productId(), memberId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
        }

        String memberNickname;
        try {
            memberNickname = memberClient.getMemberNickname(memberId);
            if (memberNickname == null) {
                throw new ReviewException(ReviewErrorCode.MEMBER_NICKNAME_NOT_FOUND);
            }
        } catch (FeignException.NotFound e) {
            throw new ReviewException(ReviewErrorCode.MEMBER_NICKNAME_NOT_FOUND);
        } catch (FeignException e) {
            throw new ReviewException(ReviewErrorCode.MEMBER_SERVICE_UNAVAILABLE);
        }

        boolean isProductValid; //상품 상태 체크
        try {
            isProductValid = productClient.canPostReview(command.productId()); // 상품 상태 체크
            if (!isProductValid) {
                throw new ReviewException(ReviewErrorCode.PRODUCT_DISCONTINUED);
            }
        } catch (FeignException e) {
            throw new ReviewException(ReviewErrorCode.PRODUCT_DISCONTINUED);
        }

        if (!reviewRepository.existsByProductId(command.productId())) {
            try {
                reviewRepository.save(ReviewStatic.builder()
                        .productId(command.productId())
                        .build());
            } catch (DataIntegrityViolationException ignored) {
            }
        }

        // TODO: 상품 상태 체크 로직을 OpenFeign 등을 활용하여 구현 필요 (boolean)
        boolean isOrderValid; // 주문 상태 체크

        reviewRepository.incrementStatistics(command.productId(), command.rating());

        ReviewDetail reviewDetail = ReviewDetail.builder()
                .memberId(memberId)
                .nickname(memberNickname)
                .productId(command.productId())
                .orderId(command.orderId())
                .rating(command.rating())
                .body(command.body())
                .build();

        reviewDetailRepository.save(reviewDetail);
        ReviewCreatedEvent event = new ReviewCreatedEvent(reviewDetail.getId(), reviewDetail.getBody());
        eventPublisher.publishEvent(event);
        return ReviewIdInfo.from(reviewDetail.getId());
    }

    // 상품에 대한 리뷰 통계 정보 조회
    public ReviewInfo getReviewInfo(UUID productId) {
        ReviewStatic review = reviewRepository.findByProductId(productId);
        if (review == null) {
            throw new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND);
        }
        BigDecimal averageRating = review.calculateAverageRating();
        return ReviewInfo.from(review, averageRating);
    }

    // 상품에 대한 리뷰 통계 정보 삭제
    @Transactional
    public void deleteReviewStatic(ProductDiscontinuedEvent event) {
        reviewRepository.deleteByProductId(event.productId());
    }

    // 상품에 리뷰 상세 정보 조회 (최신순)
    public Page<ReviewDetailInfo> getReviewDetails(UUID productId, Pageable pageable) {
        Page<ReviewDetail> reviewDetailsPage = reviewDetailRepository.findAllLatestByProduct(productId, pageable);
        return reviewDetailsPage.map(ReviewDetailInfo::from);
    }

    // 리뷰 상세 정보 수정
    @Transactional
    public ReviewIdInfo updateReviewDetail(UUID memberId, UUID reviewId, ReviewUpdateCommand command) {
        ReviewDetail reviewDetail = reviewDetailRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        //삭제된 리뷰인지 검증
        reviewDetail.validateNotDeleted();
        //수정 권한 검증
        reviewDetail.validateMember(memberId);
        //통계 정보 반영
        if (reviewDetail.getRating() != command.rating()) {
            reviewRepository.updateStatisticsOnReviewEdit(
                    reviewDetail.getProductId(),
                    reviewDetail.getRating(),
                    command.rating()
            );
        }
        //리뷰 수정
        reviewDetail.update(command);
        ReviewCreatedEvent event = new ReviewCreatedEvent(reviewDetail.getId(), reviewDetail.getBody());
        eventPublisher.publishEvent(event);
        return ReviewIdInfo.from(reviewDetail.getId());
    }

    // 리뷰 상세 정보 삭제
    @Transactional
    public void deleteReviewDetail(UUID memberId, UUID reviewId) {
        ReviewDetail reviewDetail = reviewDetailRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));
        //삭제된 리뷰인지 검증
        reviewDetail.validateNotDeleted();
        //수정 권한 검증
        reviewDetail.validateMember(memberId);
        // 통계 정보 반영
        reviewRepository.decrementStatistics(reviewDetail.getProductId(), reviewDetail.getRating());
        // 논리 삭제 처리
        reviewDetail.delete();
    }

    // 회원의 리뷰 상세 정보 조회
    public Page<ReviewDetailInfo> getMyReviewDetails(UUID memberId, Pageable pageable) {
        Page<ReviewDetail> reviewDetailsPage = reviewDetailRepository.findAllLatestByMember(memberId,  pageable);
        return reviewDetailsPage.map(ReviewDetailInfo::from);
    }

    // 리뷰 공감하기
    @Transactional
    public ReviewIdInfo likeReview(UUID memberId, UUID reviewId) {
        ReviewDetail reviewDetail = reviewDetailRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        //삭제된 리뷰인지 검증
        reviewDetail.validateNotDeleted();
        // 본인의 리뷰에 공감할 수 없음
        reviewDetail.validateSelfLike(memberId);

        // 이미 공감했는지 확인
        if (reviewLikeRepository.existsByReviewIdAndMemberId(reviewId, memberId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_ALREADY_LIKED);
        }

        // 공감 기록 저장
        reviewLikeRepository.save(ReviewLikeHistory.builder()
                .reviewId(reviewId)
                .memberId(memberId)
                .build());

        // 리뷰의 공감 수 증가
        reviewDetailRepository.incrementLikeCount(reviewId);
        return ReviewIdInfo.from(reviewDetail.getId());
    }

    // 리뷰 요약용 임베딩 생성 이벤트 처리
    @Transactional
    public void createReviewEmbedding(ReviewCreatedEvent event) {
        try {
            ReviewDetail review = reviewDetailRepository.findById(event.reviewId())
                    .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));
            String cleanedBody = event.body().replaceAll("\\s+", " ").trim();
            float[] vector = embeddingModel.embed(cleanedBody);
            String vectorString = Arrays.toString(vector);
            reviewDetailRepository.updateReviewEmbedding(review.getId(), vectorString);
            log.info("리뷰 임베딩 생성 및 업데이트 완료: {}", event.reviewId());
        } catch (Exception e) {
            log.error("리뷰 임베딩 생성 실패: {}, ReviewId: {}", e, event.reviewId());
            throw e;
        }
    }

    //테스트용 전체 임베딩 재생성
    @Transactional
    public void recreateAllReviewEmbeddings() {
        List<ReviewDetail> reviews = reviewDetailRepository.findReviewsWithoutEmbedding();
        for (ReviewDetail review : reviews) {
            try {
                String cleanedBody = review.getBody().replaceAll("\\s+", " ").trim();
                float[] vector = embeddingModel.embed(cleanedBody);
                String vectorString = Arrays.toString(vector);
                reviewDetailRepository.updateReviewEmbedding(review.getId(), vectorString);
                log.info("리뷰 임베딩 생성 및 업데이트 완료: {}", review.getId());
            } catch (Exception e) {
                log.error("리뷰 임베딩 생성 실패: {}, ReviewId: {}", e, review.getId());
                throw e;
            }
        }
    }

    // 공감 수 1위인 특정 상품 리뷰와 유사한 리뷰 검색
    public List<ReviewDetailInfo> searchSimilarReviewDetails(UUID productId, ReviewSearchSimilarCommand command) {
        long reviewCount;
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (command.referenceMonth() == 0) {
            reviewCount = reviewDetailRepository.countReviews(productId);
        } else {
            LocalDate date = LocalDate.of(command.referenceYear(), command.referenceMonth(), 1);
            start = date.atStartOfDay();
            end = date.withDayOfMonth(date.lengthOfMonth()).atTime(LocalTime.MAX);
            reviewCount = reviewDetailRepository.countReviewsBetween(productId, start, end);
        }
        //리뷰가 10개 미만일 경우 예외 처리
        if (reviewCount < 10) {
            throw new ReviewException(ReviewErrorCode.REVIEW_NOT_ENOUGH_FOR_SIMILARITY_SEARCH);
        }

        ReviewDetail topLikedReview;
        if (command.referenceMonth() == 0) {
            // 전체 기간 동안 공감 수 1위 리뷰 조회
            topLikedReview = reviewDetailRepository.findTopReview(productId)
                    .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));
        } else {
            // 최근 1개월 동안 공감 수 1위 리뷰 조회
            topLikedReview = reviewDetailRepository.findTopReviewBetween(productId, start, end)
                    .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));
        }

        // 공감 수가 0인 경우 예외 처리
        if (topLikedReview.getLikeCount() == 0) {
            throw new ReviewException(ReviewErrorCode.REVIEW_NO_LIKES);
        }
        // 유사한 리뷰 조회
        List<ReviewDetail> similarReviews = reviewDetailRepository.findSimilarReviews(
                productId,
                topLikedReview.getId()
        );
        similarReviews.add(0, topLikedReview);
        return similarReviews.stream()
                .map(ReviewDetailInfo::from)
                .toList();
    }
}

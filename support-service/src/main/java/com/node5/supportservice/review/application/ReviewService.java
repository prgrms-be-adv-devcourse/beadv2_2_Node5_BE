package com.node5.supportservice.review.application;

import com.node5.supportservice.review.application.dto.*;
import com.node5.supportservice.review.client.MemberClient;
import com.node5.supportservice.review.domain.*;
import com.node5.supportservice.review.exception.ReviewErrorCode;
import com.node5.supportservice.review.exception.ReviewException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewDetailRepository reviewDetailRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    private final MemberClient memberClient;

    // 상품에 리뷰 작성
    @Transactional
    public ReviewIdInfo createReviewDetail(UUID memberId, ReviewCreateCommand command) {

        if (reviewDetailRepository.existsByProductIdAndMemberId(command.productId(), memberId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
        }

        String memberNickname;
        try {
            memberNickname = memberClient.getMemberNickname(memberId.toString()).getBody();
            if (memberNickname == null) {
                throw new ReviewException(ReviewErrorCode.MEMBER_NICKNAME_NOT_FOUND);
            }
        } catch (FeignException.NotFound e) {
            throw new ReviewException(ReviewErrorCode.MEMBER_NICKNAME_NOT_FOUND);
        } catch (FeignException e) {
            throw new ReviewException(ReviewErrorCode.MEMBER_SERVICE_UNAVAILABLE);
        }

        // TODO: 상품 주문 상태와, 상품 상태 체크 로직을 OpenFeign 등을 활용하여 구현 필요 (boolean)
        boolean isOrderValid; // 주문 상태 체크
        boolean isProductValid; // 상품 상태 체크

        if (!reviewRepository.existsByProductId(command.productId())) {
            try {
                reviewRepository.save(Review.builder()
                        .productId(command.productId())
                        .build());
            } catch (DataIntegrityViolationException ignored) {
            }
        }



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
        return ReviewIdInfo.from(reviewDetail.getId());
    }

    // 상품에 리뷰 통계 정보 조회
    public ReviewInfo getReviewInfo(UUID memberId, UUID productId) {
        Review review = reviewRepository.findByProductId(productId);
        if (review == null) {
            throw new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND);
        }
        BigDecimal averageRating = review.calculateAverageRating();
        return ReviewInfo.from(review, averageRating);
    }

    // 상품에 리뷰 상세 정보 조회
    public Page<ReviewDetailInfo> getReviewDetails(UUID memberId, UUID productId, Pageable pageable) {
        Page<ReviewDetail> reviewDetailsPage = reviewDetailRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
        return reviewDetailsPage.map(ReviewDetailInfo::from);
    }

    // 리뷰 상세 정보 수정
    @Transactional
    public ReviewIdInfo updateReviewDetail(UUID memberId, UUID reviewId, ReviewUpdateCommand command) {
        ReviewDetail reviewDetail = reviewDetailRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        reviewDetail.validateMember(memberId);
        if (reviewDetail.getRating() != command.rating()) {
            reviewRepository.updateStatisticsOnReviewEdit(
                    reviewDetail.getProductId(),
                    reviewDetail.getRating(),
                    command.rating()
            );
        }
        reviewDetail.update(command);
        return ReviewIdInfo.from(reviewDetail.getId());
    }

    // 리뷰 상세 정보 삭제
    @Transactional
    public void deleteReviewDetail(UUID memberId, UUID reviewId) {
        ReviewDetail reviewDetail = reviewDetailRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));
        reviewDetail.validateMember(memberId);

        reviewRepository.decrementStatistics(reviewDetail.getProductId(), reviewDetail.getRating());
        reviewDetailRepository.deleteById(reviewDetail.getId());
    }

    // 회원의 리뷰 상세 정보 조회
    public Page<ReviewDetailInfo> getMyReviewDetails(UUID memberId, Pageable pageable) {
        Page<ReviewDetail> reviewDetailsPage = reviewDetailRepository.findByMemberIdOrderByCreatedAtDesc(memberId,  pageable);
        return reviewDetailsPage.map(ReviewDetailInfo::from);
    }

    // 리뷰 공감하기
    @Transactional
    public ReviewIdInfo likeReview(UUID memberId, UUID reviewId) {
        ReviewDetail reviewDetail = reviewDetailRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        // 본인의 리뷰에 공감할 수 없음
        if (reviewDetail.getMemberId().equals(memberId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_CANNOT_LIKE_OWN);
        }

        // 이미 공감했는지 확인
        if (reviewLikeRepository.existsByReviewIdAndMemberId(reviewId, memberId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_ALREADY_LIKED);
        }

        // 공감 기록 저장
        reviewLikeRepository.save(ReviewLike.builder()
                .reviewId(reviewId)
                .memberId(memberId)
                .build());

        // 리뷰의 공감 수 증가
        reviewDetailRepository.incrementLikeCount(reviewId);
        return ReviewIdInfo.from(reviewDetail.getId());
    }

}

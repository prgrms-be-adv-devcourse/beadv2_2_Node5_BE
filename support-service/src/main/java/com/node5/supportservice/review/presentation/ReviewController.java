package com.node5.supportservice.review.presentation;

import com.node5.supportservice.review.application.ReviewService;
import com.node5.supportservice.review.application.dto.ReviewDetailInfo;
import com.node5.supportservice.review.application.dto.ReviewIdInfo;
import com.node5.supportservice.review.application.dto.ReviewInfo;
import com.node5.supportservice.review.application.dto.ReviewStatusInfo;
import com.node5.supportservice.review.presentation.dto.ReviewCreateRequest;
import com.node5.supportservice.review.presentation.dto.ReviewStatusRequest;
import com.node5.supportservice.review.presentation.dto.ReviewUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("${api.v1}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 저장", description = "회원의 리뷰를 저장한다.")
    @PostMapping
    public ResponseEntity<ReviewIdInfo> createReview(@RequestHeader ("Member-Id") UUID memberId, @RequestBody ReviewCreateRequest request) {

        return ResponseEntity.status(CREATED).body(reviewService.createReviewDetail(memberId, request.toCommand()));
    }

    @Operation(summary = "리뷰 통계 조회", description = "상품의 리뷰 통계 정보를 조회한다.")
    @GetMapping("/static/{productId}")
    public ResponseEntity<ReviewInfo> getReview(@PathVariable UUID productId) {
        return ResponseEntity.ok(reviewService.getReviewInfo(productId));
    }

    @Operation(summary = "리뷰 상세 조회", description = "상품의 리뷰 상세 정보를 조회한다.")
    @GetMapping("/detail/{productId}")
    public ResponseEntity<Page<ReviewDetailInfo>> getReviewDetails(@PathVariable UUID productId,
                                                                   @RequestParam String orderBy,
                                                                   Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewDetails(productId, orderBy, pageable));
    }

    @Operation(summary = "리뷰 수정", description = "회원의 리뷰를 수정한다.")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewIdInfo> updateReview(@RequestHeader("Member-Id") UUID memberId,
                                                     @PathVariable UUID reviewId,
                                                     @RequestBody ReviewUpdateRequest request) {
        return ResponseEntity.ok(reviewService.updateReviewDetail(memberId, reviewId, request.toCommand()));
    }

    @Operation(summary = "리뷰 삭제", description = "회원의 리뷰를 삭제한다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(@RequestHeader("Member-Id") UUID memberId,
                                                   @PathVariable UUID reviewId) {
        reviewService.deleteReviewDetail(memberId, reviewId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내가 작성한 리뷰 조회", description = "회원이 작성한 리뷰 상세 정보를 조회한다.")
    @GetMapping("/my")
    public ResponseEntity<Page<ReviewDetailInfo>> getMyReviews(@RequestHeader("Member-Id") UUID memberId,
                                                              Pageable pageable) {
        return ResponseEntity.ok(reviewService.getMyReviewDetails(memberId, pageable));
    }

    @Operation(summary = "리뷰 공감하기", description = "회원이 리뷰에 공감한다.")
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<ReviewIdInfo> likeReview(@RequestHeader("Member-Id") UUID memberId,
                                                   @PathVariable UUID reviewId) {
        return ResponseEntity.ok(reviewService.likeReview(memberId, reviewId));
    }

    @Operation(summary = "상품 리뷰 작성 상태 조회", description = "회원이 해당 상품에 대해 리뷰를 작성했는지 조회한다.")
    @GetMapping("/reviewed")
    public ResponseEntity<ReviewStatusInfo> hasMemberReviewedProduct(@RequestHeader("Member-Id") UUID memberId, @RequestParam("orderId") UUID orderId, @RequestParam("productId") UUID productId) {
        return ResponseEntity.ok(reviewService.hasMemberReviewedProduct(memberId, orderId, productId));
    }
}

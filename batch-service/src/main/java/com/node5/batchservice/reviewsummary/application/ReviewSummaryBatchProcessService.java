package com.node5.batchservice.reviewsummary.application;

import com.node5.batchservice.reviewsummary.application.dto.ReviewContext;
import com.node5.batchservice.reviewsummary.client.LLMChatClient;
import com.node5.batchservice.reviewsummary.client.SupportClient;
import com.node5.batchservice.reviewsummary.client.dto.ReviewDetailInfo;
import com.node5.batchservice.reviewsummary.client.dto.ReviewSearchSimilarRequest;
import com.node5.batchservice.reviewsummary.client.dto.ReviewSummaryInfoResponse;
import com.node5.batchservice.reviewsummary.client.dto.ReviewSummaryUpsertRequest;
import com.node5.batchservice.reviewsummary.exception.NoReviewException;
import com.node5.batchservice.reviewsummary.exception.ReviewSummaryBatchErrorCode;
import com.node5.batchservice.reviewsummary.exception.ReviewSummaryBatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewSummaryBatchProcessService {
    private final SupportClient supportClient;
    private final LLMChatClient chatClient;
    private final String reviewSummaryTemplate;
//    private final ReviewTestData testData;

    public ReviewSummaryUpsertRequest process(UUID productId, LocalDate batchDate) {

        ReviewContext ctx = loadContext(productId, batchDate);
        String summary = generateSummary(ctx);

        return new ReviewSummaryUpsertRequest(
                productId,
                summary,
                ctx.summaryEndDate()
        );
    }

    private ReviewContext loadContext(UUID productId, LocalDate batchDate) {

        LocalDate summaryDate = batchDate.minusMonths(1);
        int year = summaryDate.getYear();
        int month = summaryDate.getMonthValue();

        ResponseEntity<ReviewSummaryInfoResponse> reviewSummaryResponse =
                supportClient.getReviewSummary(productId);

        if (!reviewSummaryResponse.getStatusCode().is2xxSuccessful()) {
            throw new ReviewSummaryBatchException(ReviewSummaryBatchErrorCode.GET_REVIEW_SUMMARY_FAILED);
        }

        ReviewSummaryInfoResponse lastSummary = reviewSummaryResponse.getBody();

        String prevSummary = lastSummary == null ? "없음" : lastSummary.summary();

        ReviewSearchSimilarRequest request =
                lastSummary == null
                        ? new ReviewSearchSimilarRequest(0, 0)
                        : new ReviewSearchSimilarRequest(year, month);

        ResponseEntity<List<ReviewDetailInfo>> reviewDetailResponse = supportClient.searchSimilarReviews(productId, request);

        if (!reviewDetailResponse.getStatusCode().is2xxSuccessful()) {
            throw new ReviewSummaryBatchException(ReviewSummaryBatchErrorCode.GET_REVIEWS_FAILED);
        }

        List<ReviewDetailInfo> reviewDetails = reviewDetailResponse.getBody();

        if (reviewDetails == null || reviewDetails.isEmpty()) {
            throw new NoReviewException(productId, year, month);
        }

        List<String> reviews = reviewDetails.stream()
                .map(ReviewDetailInfo::body)
                .toList();

//        List<String> reviews = testData.getReviews(productId);

        LocalDate endDate = summaryDate.withDayOfMonth(summaryDate.lengthOfMonth());

        return new ReviewContext(productId, prevSummary, reviews, endDate);
    }

    private String generateSummary(ReviewContext ctx) {

        String prompt = reviewSummaryTemplate
                .replace("{{prev_summary}}", ctx.prevSummary())
                .replace("{{reviews}}",
                        ctx.reviews().stream()
                                .map(r -> "- " + r)
                                .collect(Collectors.joining("\n"))
                );

        String summary = chatClient.reviewSummary(prompt);

        if (summary == null || summary.isBlank()) {
            throw new ReviewSummaryBatchException(ReviewSummaryBatchErrorCode.SUMMARY_FAILED);
        }

        return summary;
    }
}

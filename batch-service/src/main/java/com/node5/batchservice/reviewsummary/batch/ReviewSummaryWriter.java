package com.node5.batchservice.reviewsummary.batch;

import com.node5.batchservice.reviewsummary.client.SupportClient;
import com.node5.batchservice.reviewsummary.client.dto.ReviewSummaryUpsertRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReviewSummaryWriter implements ItemWriter<ReviewSummaryUpsertRequest> {

    private final SupportClient supportClient;

    @Override
    public void write(@NonNull Chunk<? extends ReviewSummaryUpsertRequest> chunk) {
        chunk.getItems().forEach(supportClient::upsertReviewSummary);
    }
}

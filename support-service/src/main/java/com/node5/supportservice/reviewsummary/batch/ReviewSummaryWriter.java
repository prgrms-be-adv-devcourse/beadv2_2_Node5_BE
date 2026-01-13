package com.node5.supportservice.reviewsummary.batch;

import com.node5.supportservice.reviewsummary.batch.dto.ReviewSummaryCommand;
import com.node5.supportservice.reviewsummary.domain.ReviewSummary;
import com.node5.supportservice.reviewsummary.domain.ReviewSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewSummaryWriter implements ItemWriter<ReviewSummaryCommand> {

    private final ReviewSummaryRepository reviewSummaryRepository;

    @Override
    @Transactional
    public void write(@NonNull Chunk<? extends ReviewSummaryCommand> chunk) {
        chunk.getItems().forEach(command -> {
            Optional<ReviewSummary> summary = reviewSummaryRepository.findByProductId(command.productId());

            if (summary.isPresent()) {
                summary.get().update(command.summary(), command.endDate());
            } else {
                ReviewSummary newSummary = ReviewSummary.create(command.productId(), command.summary(), command.startDate(), command.endDate());
                reviewSummaryRepository.save(newSummary);
            }
        });
    }
}

package com.node5.batchservice.subscription.batch;

import com.node5.batchservice.subscription.client.OrderClient;
import com.node5.batchservice.subscription.client.dto.SubscriptionBatchTarget;
import com.node5.common.domain.PagedResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

@Slf4j
public class SubscriptionBatchTargetReader implements ItemReader<SubscriptionBatchTarget> {

    private final OrderClient orderClient;
    private final String runDate;
    private final int pageSize;

    private int page = 0;
    private int index = 0;
    private int totalPages = Integer.MAX_VALUE;
    private List<SubscriptionBatchTarget> current = Collections.emptyList();

    public SubscriptionBatchTargetReader(OrderClient orderClient,
                                         String runDate,
                                         int pageSize) {
        this.orderClient = orderClient;
        this.runDate = runDate;
        this.pageSize = pageSize;
    }

    @Override
    public SubscriptionBatchTarget read() {
        if (index >= current.size()) {
            if (page >= totalPages) {
                return null;
            }

            ResponseEntity<PagedResponseDto<SubscriptionBatchTarget>> response =
                    orderClient.findTargets(runDate, page, pageSize);
            PagedResponseDto<SubscriptionBatchTarget> payload = response.getBody();

            if (payload == null || payload.content().isEmpty()) {
                return null;
            }

            current = payload.content();
            index = 0;
            totalPages = payload.pageInfo().totalPages();
            page += 1;
        }

        return current.get(index++);
    }
}

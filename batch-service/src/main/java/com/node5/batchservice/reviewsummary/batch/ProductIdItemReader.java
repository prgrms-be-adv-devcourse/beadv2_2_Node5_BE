package com.node5.batchservice.reviewsummary.batch;

import com.node5.batchservice.reviewsummary.client.CatalogClient;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class ProductIdItemReader implements ItemReader<UUID>, ItemStream {

    private final CatalogClient catalogClient;

    private static final int PAGE_SIZE = 100;

    private int page;
    private int index;
    private List<UUID> productIds = Collections.emptyList();

    @Override
    public @Nullable UUID read() {
        if(index >= productIds.size()) {
            PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);

            List<UUID> result;
            try {
                result = catalogClient.getProductIds(pageRequest).getBody();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (result == null || result.isEmpty()) {return null;}

            productIds = result;
            index = 0;
            page++;
        }
        return productIds.get(index++);
    }

    @Override
    public void open(ExecutionContext executionContext) {
        this.page = executionContext.getInt("page", 0);
        this.index = 0;
        this.productIds = Collections.emptyList();
    }

    @Override
    public void update(ExecutionContext executionContext) {
       executionContext.putInt("page", page);
    }
}

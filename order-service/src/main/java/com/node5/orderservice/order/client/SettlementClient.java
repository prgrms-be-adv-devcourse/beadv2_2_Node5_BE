package com.node5.orderservice.order.client;

import com.node5.orderservice.order.client.dto.SettlementSourceItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "settlement-service")
public interface SettlementClient {

    @PostMapping("/internal/settlements/source")
    ResponseEntity<Void> settle(@RequestBody List<SettlementSourceItem> items);

}

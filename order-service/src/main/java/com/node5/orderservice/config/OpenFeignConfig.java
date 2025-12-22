package com.node5.orderservice.config;

import com.node5.orderservice.order.client.BillingClient;
import com.node5.orderservice.order.client.BillingErrorDecoder;
import com.node5.orderservice.order.client.CatalogClient;
import com.node5.orderservice.order.client.SettlementClient;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {BillingClient.class, CatalogClient.class, SettlementClient.class})
public class OpenFeignConfig {

    @Bean
    public ErrorDecoder billingErrorDecoder() {
        return new BillingErrorDecoder();
    }
}
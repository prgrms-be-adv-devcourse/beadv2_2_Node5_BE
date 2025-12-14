package com.node5.settlementservice.config;

import com.node5.settlementservice.client.BillingClient;
import com.node5.settlementservice.client.BillingErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = BillingClient.class)
public class OpenFeignConfig {

    @Bean
    public ErrorDecoder billingErrorDecoder() {
        return new BillingErrorDecoder();
    }
}

package com.node5.settlementservice.config;

import com.node5.settlementservice.settlement.client.*;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {WalletClient.class, MemberClient.class, OrderClient.class, CatalogClient.class})
public class OpenFeignConfig {

    @Bean
    public ErrorDecoder CommonFeignDecoder() {
        return new CommonFeignDecoder();
    }
}

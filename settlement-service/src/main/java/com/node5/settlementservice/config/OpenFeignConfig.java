package com.node5.settlementservice.config;

import com.node5.settlementservice.settlement.client.WalletClient;
import com.node5.settlementservice.settlement.client.CommonFeignDecoder;
import com.node5.settlementservice.settlement.client.ShopClient;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {WalletClient.class, ShopClient.class})
public class OpenFeignConfig {

    @Bean
    public ErrorDecoder CommonFeignDecoder() {
        return new CommonFeignDecoder();
    }
}

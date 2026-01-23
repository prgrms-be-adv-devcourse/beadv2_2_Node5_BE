package com.node5.memberservice.settlement.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenFeignConfig {

    @Bean
    public ErrorDecoder CommonFeignDecoder() {
        return new CommonFeignDecoder();
    }
}


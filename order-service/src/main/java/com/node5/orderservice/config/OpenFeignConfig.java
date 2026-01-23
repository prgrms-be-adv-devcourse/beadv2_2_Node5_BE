package com.node5.orderservice.config;

import com.node5.orderservice.global.openfeign.client.WalletClient;
import com.node5.orderservice.global.openfeign.client.CatalogClient;
import com.node5.orderservice.global.openfeign.client.MemberClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {WalletClient.class, CatalogClient.class, MemberClient.class})
public class OpenFeignConfig {

}
package com.node5.apigateway.filter;

import io.jsonwebtoken.Claims;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(AuthenticationFilter.Config config) {
        return (exchange, chain) -> {
            Object attribute = exchange.getAttribute("cached_claims");

            if (!(attribute instanceof Claims claims)) {
                return chain.filter(exchange);
            }

            String memberId = claims.getSubject();
            String status = claims.get("memberStatus", String.class);

            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("Member-Id", memberId)
                    .header("Member-Status", status)
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    @Data
    public static class Config {

    }

}

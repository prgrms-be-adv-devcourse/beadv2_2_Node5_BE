package com.node5.apigateway.filter;

import io.jsonwebtoken.Claims;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Component
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    public AuthorizationFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(AuthorizationFilter.Config config) {
        return (exchange, chain) -> {
            Object attribute = exchange.getAttribute("cached_claims");

            if (!(attribute instanceof Claims claims)) {
                return chain.filter(exchange);
            }

            ServerHttpRequest request = exchange.getRequest();

            return authorizeByMemberService(claims.getSubject(), request)
                    .flatMap(allowed -> {
                        if (allowed) {
                            return chain.filter(exchange);
                        }
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    })
                    .onErrorResume(ex -> {
                        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                        return exchange.getResponse().setComplete();
                    });
        };
    }

    private Mono<Boolean> authorizeByMemberService(String memberId, ServerHttpRequest request) {
        return webClientBuilder.build()
                .post()
                .uri("lb://member-service/internal/auth/authorize")
                .bodyValue(new AuthorizeRequest(
                        UUID.fromString(memberId),
                        request.getMethod().name(),
                        request.getPath().pathWithinApplication().value()
                ))
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofMillis(500));
    }

    record AuthorizeRequest(
            UUID memberId,
            String method,
            String path
    ) {
    }

    @Data
    public static class Config {

    }

}

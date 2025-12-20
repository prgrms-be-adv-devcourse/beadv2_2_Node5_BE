package com.node5.apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


@Slf4j
@Component
public class ReactiveAuthorization implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final SecretKey secretKey;
    private final WebClient.Builder webClientBuilder;

    public ReactiveAuthorization(@Value("${token.secret}") String secret, WebClient.Builder builder) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.webClientBuilder = builder;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        ServerHttpRequest request = context.getExchange().getRequest();
        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || authorizationHeader.isBlank() || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("인증 헤더가 없거나 형식이 올바르지 않습니다.");
            return Mono.just(new AuthorizationDecision(false));
        }

        String token = authorizationHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (!"ACCESS".equals(claims.get("type", String.class))) {
                return Mono.just(new AuthorizationDecision(false));
            }

            if (claims.getSubject() == null || claims.getSubject().isBlank()) {
                return Mono.just(new AuthorizationDecision(false));
            }

            if (!(claims.get("memberStatus") instanceof String memberStatus) || memberStatus.isBlank()) {
                return Mono.just(new AuthorizationDecision(false));
            }

            context.getExchange().getAttributes().put("cached_claims", claims);

            return authorizeByMemberService(claims.getSubject(), request)
                    .map(AuthorizationDecision::new)
                    .onErrorReturn(new AuthorizationDecision(false));
        } catch (Exception e) {
            return Mono.just(new AuthorizationDecision(false));
        }
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
                .bodyToMono(Boolean.class);
    }

    record AuthorizeRequest(
            UUID memberId,
            String method,
            String path
    ) {
    }
}

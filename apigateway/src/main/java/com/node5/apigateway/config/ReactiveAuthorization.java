package com.node5.apigateway.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Component
public class ReactiveAuthorization implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Value("${apigateway.host:http://localhost:8000}")
    private String APIGATEWAY_HOST;

//    private static final String AUTH_SERVICE_ID = "http://member-service.default.svc.cluster.local:8081";

    @Value("${token.secret}")
    private String TOKEN_SECRET;

    public static final String AUTHORIZATION_URI = "/member-service" + "/api/v1/authorizations/check";
//    public static final String AUTHORIZATION_URI = "/api/v1/authorizations/check";
    public static final String REFRESH_TOKEN_URI = "/user-service" + "/api/v1/users/token/refresh";


    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        ServerHttpRequest request = context.getExchange().getRequest();
        RequestPath requestPath = request.getPath();
        HttpMethod httpMethod = request.getMethod();

        String baseUrl =
                APIGATEWAY_HOST + AUTHORIZATION_URI + "?httpMethod=" + httpMethod + "&requestPath="
                        + requestPath;
        log.info("baseUrl={}", baseUrl);

        String authorizationHeader = "";

        List<String> authorizations =
                request.getHeaders().getOrDefault(HttpHeaders.AUTHORIZATION, null);

        if (authorizations != null && !authorizations.isEmpty()
                && StringUtils.hasLength(authorizations.get(0))
                && !"undefined".equals(authorizations.get(0))
        ) {
            try {
                authorizationHeader = authorizations.get(0);
                String jwt = authorizationHeader.replace("Bearer ", "");
                String subject = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(TOKEN_SECRET))).build()
                        .parseSignedClaims(jwt)
                        .getPayload()
                        .getSubject();

                // refresh token 요청 시 토큰 검증만 하고 인가 처리 한다.
                if (REFRESH_TOKEN_URI.equals(requestPath + "")) {
                    return Mono.just(new AuthorizationDecision(true));
                }
                if (subject == null || subject.isEmpty()) {
                    log.error("토큰 인증 오류");
                    throw new AuthorizationServiceException("토큰 인증 오류");
                }
            } catch (IllegalArgumentException e) {
                log.error("토큰 헤더 오류 : {}", e.getMessage());
                throw new AuthorizationServiceException("토큰 인증 오류");
            } catch (ExpiredJwtException e) {
                log.error("토큰 유효기간이 만료되었습니다. : {}", e.getMessage());
                throw new AuthorizationServiceException("토큰 유효기간 만료");
            } catch (Exception e) {
                log.error("토큰 인증 오류 Exception : {}", e.getMessage());
                throw new AuthorizationServiceException("토큰 인증 오류");
            }
        }

        String token = authorizationHeader;

        return WebClient.create(baseUrl)
                .get()
                .headers(httpHeaders -> httpHeaders.add(HttpHeaders.AUTHORIZATION, token))
                .retrieve()
                .bodyToMono(Boolean.class)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false))
                .onErrorResume(e -> {
                    log.error("인가 서버 요청 중 오류: {}", e.getMessage());
                    return Mono.just(new AuthorizationDecision(false));
                });

//        boolean granted = false;
//        try {
//            String token = authorizationHeader; // Variable used in lambda expression should be final or effectively final
//            Mono<Boolean> body = WebClient.create(baseUrl)
//                    .get()
//                    .headers(httpHeaders -> {
//                        httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
//                    })
//                    .retrieve().bodyToMono(Boolean.class);
//            granted = body.toFuture().get().booleanValue();
//            log.info("Security AuthorizationDecision granted={}", granted);
//        } catch (Exception e) {
//            log.error("인가 서버에 요청 중 오류 : {}", e.getMessage());
//            throw new AuthorizationServiceException("인가 요청시 오류 발생");
//        }
//
//        return Mono.just(new AuthorizationDecision(granted));
    }

}

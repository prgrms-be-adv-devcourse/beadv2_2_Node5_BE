package com.node5.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authorization.AuthorizationContext;


@Configuration
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {

    private final static String[] PERMITALL_ANTPATTERNS = {
            ReactiveAuthorization.AUTHORIZATION_URI, "/", "/csrf",
            "/?*-service/swagger-ui/**",
            "/?*-service/actuator/?*", "/actuator/?*",
            "/v3/api-docs/**", "/?*-service/v3/api-docs", "/swagger*/**", "/webjars/**"
    };
    private final static String USER_JOIN_ANTPATTERNS = "/member-service/api/v1/members";


    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http, ReactiveAuthorizationManager<AuthorizationContext> check) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .headers(headers -> headers.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable))
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(basic -> basic.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(PERMITALL_ANTPATTERNS).permitAll()
                        .pathMatchers(HttpMethod.POST, USER_JOIN_ANTPATTERNS).permitAll()
                        .anyExchange().access(check)
                );

        return http.build();
    }
}

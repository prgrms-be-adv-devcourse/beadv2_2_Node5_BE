package com.node5.apigateway.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties.SwaggerUrl;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class OpenApiDocConfig {

    private final SwaggerUiConfigProperties swaggerUiConfigProperties;
    private final RouteDefinitionLocator locator;

    @PostConstruct
    public void init() {
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();

        Set<SwaggerUrl> urls = new HashSet<>();

        if (definitions != null) {
            definitions.stream()
                    .filter(routeDefinition -> routeDefinition.getId().matches(".*-service"))
                    .forEach(routeDefinition -> {
                        String name = routeDefinition.getId();
                        SwaggerUrl swaggerUrl = new SwaggerUrl(name, "/" + name + "/v3/api-docs", name);
                        urls.add(swaggerUrl);
                    });

            if (swaggerUiConfigProperties.getUrls() != null) {
                swaggerUiConfigProperties.getUrls().addAll(urls);
            } else {
                swaggerUiConfigProperties.setUrls(urls);
            }
        }
    }

}


package com.node5.catalogservice.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {
	@Value("${spring.application.name}")
	private String appName;

	private static final String SECURITY_SCHEME_NAME = "BearerAuth";
	@Value("${apigateway.host}")
	private String host;
	@Bean
	public OpenAPI openAPI(){
		Server server = new Server();
		server.url(String.format("%s/%s",host, appName));
		List<Server> serverList = new ArrayList<>();
		serverList.add(server);

		SecurityScheme bearerScheme = new SecurityScheme()
			.name("Authorization")
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER);

		return new OpenAPI()
			.info(new Info().title("Catalog Service API").version("v1"))
			.servers(serverList)
			.components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, bearerScheme))
			.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
	}
}

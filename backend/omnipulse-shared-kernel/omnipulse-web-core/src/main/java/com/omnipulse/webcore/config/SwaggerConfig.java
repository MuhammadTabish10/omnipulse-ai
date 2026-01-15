package com.omnipulse.webcore.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerConfig {

	private final SwaggerProperties swaggerProperties;

	public OpenAPI openAPI() {
		final String securitySchemeName = "bearerAuth";

		return new OpenAPI()
				.info(new Info()
						.title(swaggerProperties.getTitle())
						.version(swaggerProperties.getVersion())
						.description(swaggerProperties.getDescription())
						.contact(new Contact()
								.name(swaggerProperties.getContactName())
								.email(swaggerProperties.getContactEmail())
								.url(swaggerProperties.getContactUrl()))
						.license(new License().name("Apache 2.0").url("http://springdoc.org")))

				.addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
				.components(new Components()
						.addSecuritySchemes(securitySchemeName,
								new SecurityScheme()
										.name(securitySchemeName)
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")

						));
	}
}

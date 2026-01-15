package com.omnipulse.webcore.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "omnipulse.swagger")
public class SwaggerProperties {
	private String title = "OmniPulse API";
	private String description = "Default API Description";
	private String version = "1.0.0";
	private String contactName = "OmniPulse Team";
	private String contactEmail = "tech@omnipulse.com";
	private String contactUrl = "https://omnipulse.com";
}

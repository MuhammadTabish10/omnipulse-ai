package com.omnipulse.observability.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

	public MeterRegistryCustomizer<MeterRegistry> metricsCustomizer(
			@Value("${spring.application.name:unknown-service}") String applicationName) {
		return registry -> registry.config().commonTags("application", applicationName);
	}
}

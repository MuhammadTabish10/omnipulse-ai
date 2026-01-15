package com.omnipulse.jpa.domain;

import com.omnipulse.common.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;

import java.util.Optional;

@Slf4j
public class AuditAwareImpl implements AuditorAware<String> {

	private static final String SYSTEM_AUDITOR = "SYSTEM";

	@NonNull
	@Override
	public Optional<String> getCurrentAuditor() {
		String userId = UserContext.getUserId();

		if (userId != null && !userId.isBlank()) {
			return Optional.of(userId);
		}

		log.trace("No user context found for auditing. Defaulting to '{}'", SYSTEM_AUDITOR);
		return Optional.of(SYSTEM_AUDITOR);
	}
}

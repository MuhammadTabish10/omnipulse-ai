package com.omnipulse.identity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
		UUID id,
		String externalId,
		String email,
		String firstName,
		String lastName,
		String tenantId,
		boolean isActive,
		LocalDateTime createdAt
) {}

package com.omnipulse.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiResponseCode {

	SUCCESS("0000", "Operation successful"),

	VALIDATION_FAILED("4000", "Validation failed"),
	BAD_REQUEST("4001", "Bad request"),
	UNAUTHORIZED("4003", "Unauthorized access"),
	RESOURCE_NOT_FOUND("4004", "Resource not found"),
	CONFLICT("4009", "Resource conflict"),

	INTERNAL_ERROR("5000", "An unexpected internal error occurred"),
	SERVICE_UNAVAILABLE("5003", "Service unavailable");

	private final String code;
	private final String description;
}
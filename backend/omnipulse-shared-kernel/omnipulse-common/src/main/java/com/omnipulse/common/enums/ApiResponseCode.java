package com.omnipulse.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiResponseCode {

	SUCCESS("0000", "Success"),

	VALIDATION_FAILED("4000", "Validation failed"),
	BAD_REQUEST("4001", "Bad request"),
	FORBIDDEN("4003", "Access denied"),
	RESOURCE_NOT_FOUND("4004", "Resource not found"),
	CONFLICT("4009", "Resource conflict"),

	INTERNAL_ERROR("5000", "An unexpected internal error occurred"),
	SERVICE_UNAVAILABLE("5003", "Service unavailable"),

	EMAIL_ALREADY_EXISTS("1001", "Email address is already registered"),
	USER_ALREADY_EXISTS("1002", "User profile already exists"),
	TENANT_NOT_FOUND("1003", "The specified tenant does not exist"),
	INVALID_TENANT_STATUS("1004", "Tenant is not active"),
	USER_ACCOUNT_DISABLED("1005", "User account is disabled");

	private final String code;
	private final String description;
}
package com.omnipulse.common.exception;

import com.omnipulse.common.enums.ApiResponseCode;

public class DuplicateResourceException extends OmniPulseException {
	public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
		super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue),
				ApiResponseCode.CONFLICT);
	}

	public DuplicateResourceException(String message, ApiResponseCode code) {
		super(message, code);
	}
}

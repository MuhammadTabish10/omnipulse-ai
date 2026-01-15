package com.omnipulse.common.exception;

import com.omnipulse.common.enums.ApiResponseCode;

public class ResourceNotFoundException extends OmniPulseException {
	public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
		super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
				ApiResponseCode.RESOURCE_NOT_FOUND);
	}
}

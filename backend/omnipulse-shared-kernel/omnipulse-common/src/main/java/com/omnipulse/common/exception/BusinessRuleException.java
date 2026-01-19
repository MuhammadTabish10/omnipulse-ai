package com.omnipulse.common.exception;

import com.omnipulse.common.enums.ApiResponseCode;

public class BusinessRuleException extends OmniPulseException {
	public BusinessRuleException(String message) {
		super(message, ApiResponseCode.BAD_REQUEST);
	}
}

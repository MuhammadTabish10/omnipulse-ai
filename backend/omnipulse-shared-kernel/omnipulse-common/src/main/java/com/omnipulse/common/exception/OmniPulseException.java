package com.omnipulse.common.exception;

import com.omnipulse.common.enums.ApiResponseCode;
import lombok.Getter;

@Getter
public class OmniPulseException extends RuntimeException {

	private final ApiResponseCode responseCode;

	public OmniPulseException(String message, ApiResponseCode responseCode) {
		super(message);
		this.responseCode = responseCode;
	}
}

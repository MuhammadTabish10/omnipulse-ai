package com.omnipulse.webcore.exception;

import com.omnipulse.common.dto.response.ApiResponse;
import com.omnipulse.common.dto.response.ValidationError;
import com.omnipulse.common.enums.ApiResponseCode;
import com.omnipulse.common.exception.OmniPulseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


	@ExceptionHandler(OmniPulseException.class)
	public ResponseEntity<ApiResponse<Void>> handleOmniPulseException(OmniPulseException ex, HttpServletRequest request) {
		ApiResponseCode code = ex.getResponseCode();
		HttpStatus status = resolveStatus(code);

		log.warn("Business Error: [{}] {} - Path: {}", code.getCode(), ex.getMessage(), request.getRequestURI());

		return ResponseEntity
				.status(status)
				.body(ApiResponse.error(ex.getMessage(), code.getCode()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
		List<ValidationError> validationErrors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(error -> new ValidationError(
						error.getField(),
						error.getDefaultMessage(),
						error.getRejectedValue()
				))
				.toList();

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.validationFailure(validationErrors, "Validation failed"));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleMalformedJson(HttpMessageNotReadableException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error("Malformed JSON request", ApiResponseCode.BAD_REQUEST.getCode()));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponse<Void>> handleMissingParams(MissingServletRequestParameterException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error("Missing parameter: " + ex.getParameterName(), ApiResponseCode.BAD_REQUEST.getCode()));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handle404(NoResourceFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error("Endpoint not found", ApiResponseCode.RESOURCE_NOT_FOUND.getCode()));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
		return ResponseEntity
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				.body(ApiResponse.error("Method " + ex.getMethod() + " not allowed", ApiResponseCode.BAD_REQUEST.getCode()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
		log.error("Unexpected System Error: ", ex);
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.error("An unexpected internal error occurred", ApiResponseCode.INTERNAL_ERROR.getCode()));
	}

	private HttpStatus resolveStatus(ApiResponseCode code) {
		return switch (code) {
			case SUCCESS -> HttpStatus.OK;
			case RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND;
			case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
			case VALIDATION_FAILED, BAD_REQUEST -> HttpStatus.BAD_REQUEST;
			case CONFLICT -> HttpStatus.CONFLICT;
			case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
			case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
			default -> HttpStatus.INTERNAL_SERVER_ERROR;
		};
	}
}
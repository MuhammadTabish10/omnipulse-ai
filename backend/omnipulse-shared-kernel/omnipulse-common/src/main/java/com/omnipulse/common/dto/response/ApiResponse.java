package com.omnipulse.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now();

	private boolean success;
	private String message;
	private T data;

	// Error details
	private String errorCode;
	private String debugMessage;
	private List<ValidationError> subErrors;

	// --- Static Factory Methods ---

	public static <T> ApiResponse<T> success(T data, String message) {
		return ApiResponse.<T>builder()
				.success(true)
				.message(message)
				.data(data)
				.build();
	}

	public static <T> ApiResponse<T> error(String message, String errorCode) {
		return ApiResponse.<T>builder()
				.success(false)
				.message(message)
				.errorCode(errorCode)
				.build();
	}

	public static <T> ApiResponse<T> validationFailure(List<ValidationError> errors, String message) {
		return ApiResponse.<T>builder()
				.success(false)
				.message(message)
				.errorCode("VALIDATION_FAILED")
				.subErrors(errors)
				.build();
	}
}


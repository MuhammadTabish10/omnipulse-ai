package com.omnipulse.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.omnipulse.common.enums.ApiResponseCode;
import com.omnipulse.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private Status status;
	private String errorCode;
	private LocalDateTime timestamp;
	private String message;
	private T data;

	private String debugMessage;
	private List<ValidationError> subErrors;


	public static <T> ApiResponse<T> success(T data, String message) {
		return ApiResponse.<T>builder()
				.timestamp(LocalDateTime.now())
				.status(Status.SUCCESS)
				.message(message)
				.data(data)
				.build();
	}

	public static <T> ApiResponse<T> error(String message, String errorCode) {
		return ApiResponse.<T>builder()
				.timestamp(LocalDateTime.now())
				.status(Status.FAIL)
				.message(message)
				.errorCode(errorCode)
				.build();
	}

	public static <T> ApiResponse<T> validationFailure(List<ValidationError> errors, String message) {
		return ApiResponse.<T>builder()
				.timestamp(LocalDateTime.now())
				.status(Status.FAIL)
				.message(message)
				.errorCode(ApiResponseCode.VALIDATION_FAILED.getCode())
				.subErrors(errors)
				.build();
	}

	public static <T> ApiResponse<PagedResponse<T>> successPaged(Page<T> page) {
		PagedResponse<T> pagedResponse = PagedResponse.of(page);

		return ApiResponse.<PagedResponse<T>>builder()
				.timestamp(LocalDateTime.now())
				.status(Status.SUCCESS)
				.message("Data retrieved successfully")
				.data(pagedResponse)
				.build();
	}
}


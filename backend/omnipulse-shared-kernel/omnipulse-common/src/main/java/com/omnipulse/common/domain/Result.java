package com.omnipulse.common.domain;

public sealed interface Result<T> permits Result.Success, Result.Failure {
	record Success<T>(T data) implements Result<T> {}
	record Failure<T>(String errorMessage, String errorCode) implements Result<T> {}

	default boolean isSuccess() {
		return this instanceof Success;
	}
}

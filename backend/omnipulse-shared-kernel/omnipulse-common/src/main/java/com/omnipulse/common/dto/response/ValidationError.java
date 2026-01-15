package com.omnipulse.common.dto.response;

import java.io.Serializable;

public record ValidationError(String field, String message, Object rejectedValue) implements Serializable {}


package com.omnipulse.common.util;

import lombok.experimental.UtilityClass;
import org.slf4j.MDC;

@UtilityClass
public class UserContext {

	private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();
	private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
	private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();

	public static final String KEY_USER = "userId";
	public static final String KEY_TENANT = "tenantId";
	public static final String KEY_CORRELATION = "correlationId";

	public static void setUserId(String userId) {
		if (userId == null || userId.isEmpty()) {
			CURRENT_USER.remove();
			MDC.remove(KEY_USER);
		} else {
			CURRENT_USER.set(userId);
			MDC.put(KEY_USER, userId);
		}
	}

	public static String getUserId() {
		return CURRENT_USER.get();
	}

	public static void setTenantId(String tenantId) {
		if (tenantId == null) {
			CURRENT_TENANT.remove();
			MDC.remove(KEY_TENANT);
		} else {
			CURRENT_TENANT.set(tenantId);
			MDC.put(KEY_TENANT, tenantId);
		}
	}

	public static String getTenantId() {
		return CURRENT_TENANT.get();
	}

	public static void setCorrelationId(String correlationId) {
		if (correlationId == null) {
			CORRELATION_ID.remove();
			MDC.remove(KEY_CORRELATION);
		} else {
			CORRELATION_ID.set(correlationId);
			MDC.put(KEY_CORRELATION, correlationId);
		}
	}

	public static String getCorrelationId() {
		return CORRELATION_ID.get();
	}

	public static void clear() {
		CURRENT_USER.remove();
		CURRENT_TENANT.remove();
		CORRELATION_ID.remove();
		MDC.clear();
	}
}
package com.omnipulse.security.filter;

import com.omnipulse.common.util.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class TenantContextFilter extends OncePerRequestFilter {

	private static final String HEADER_CORRELATION_ID = "X-Correlation-ID";

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,
	                                @NonNull HttpServletResponse response,
	                                @NonNull FilterChain filterChain) throws ServletException, IOException {
		try {
			String correlationId = request.getHeader(HEADER_CORRELATION_ID);
			if (correlationId == null) {
				correlationId = UUID.randomUUID().toString();
			}
			UserContext.setCorrelationId(correlationId);

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication instanceof JwtAuthenticationToken jwtAuthToken) {
				Jwt jwt = jwtAuthToken.getToken();
				String userId = jwt.getSubject();
				UserContext.setUserId(userId);
				String tenantId = jwt.getClaimAsString("tenant_id");
				if (tenantId != null) {
					UserContext.setTenantId(tenantId);
				}
				log.trace("Context set for User: {}, Tenant: {}", userId, tenantId);
			}

			filterChain.doFilter(request, response);

		} finally {
			UserContext.clear();
		}

	}
}

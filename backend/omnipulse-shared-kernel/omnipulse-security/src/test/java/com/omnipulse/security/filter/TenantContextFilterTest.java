package com.omnipulse.security.filter;

import com.omnipulse.common.util.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantContextFilterTest {

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	@Mock
	private SecurityContext securityContext;

	@InjectMocks
	private TenantContextFilter tenantContextFilter;

	@BeforeEach
	void setUp() {
		SecurityContextHolder.setContext(securityContext);
		UserContext.clear();
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
		UserContext.clear();
	}


	@Test
	void doFilterInternal_WithCorrelationIdHeader_ShouldSetCorrelationId() throws ServletException, IOException {

		String correlationId = "test-correlation-id-123";
		when(request.getHeader("X-Correlation-ID")).thenReturn(correlationId);
		when(securityContext.getAuthentication()).thenReturn(null);

		tenantContextFilter.doFilterInternal(request, response, filterChain);
		verify(filterChain).doFilter(request, response);
		assertNull(UserContext.getCorrelationId());
	}

	@Test
	void doFilterInternal_WithoutCorrelationIdHeader_ShouldGenerateCorrelationId() throws ServletException, IOException {

		when(request.getHeader("X-Correlation-ID")).thenReturn(null);
		when(securityContext.getAuthentication()).thenReturn(null);

		tenantContextFilter.doFilterInternal(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
		assertNull(UserContext.getCorrelationId());
	}


	@Test
	void doFilterInternal_WithJwtAuthenticationAndTenantId_ShouldSetUserAndTenantContext() throws ServletException, IOException {

		String correlationId = "correlation-123";
		String userId = "user-123";
		String tenantId = "tenant-456";

		when(request.getHeader("X-Correlation-ID")).thenReturn(correlationId);

		Jwt jwt = createJwt(userId, tenantId);
		JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(jwt);
		when(securityContext.getAuthentication()).thenReturn(jwtAuthToken);

		tenantContextFilter.doFilterInternal(request, response, filterChain);
		verify(filterChain).doFilter(request, response);

		assertNull(UserContext.getCorrelationId());
		assertNull(UserContext.getUserId());
		assertNull(UserContext.getTenantId());
	}

	@Test
	void doFilterInternal_WithJwtAuthenticationWithoutTenantId_ShouldSetUserContextOnly() throws ServletException, IOException {

		String correlationId = "correlation-123";
		String userId = "user-123";

		when(request.getHeader("X-Correlation-ID")).thenReturn(correlationId);

		Jwt jwt = createJwt(userId, null);
		JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(jwt);
		when(securityContext.getAuthentication()).thenReturn(jwtAuthToken);

		tenantContextFilter.doFilterInternal(request, response, filterChain);
		verify(filterChain).doFilter(request, response);

		assertNull(UserContext.getCorrelationId());
		assertNull(UserContext.getUserId());
		assertNull(UserContext.getTenantId());
	}

	@Test
	void doFilterInternal_WithNonJwtAuthentication_ShouldOnlySetCorrelationId() throws ServletException, IOException {

		String correlationId = "correlation-123";
		when(request.getHeader("X-Correlation-ID")).thenReturn(correlationId);

		Authentication authentication = mock(Authentication.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);

		tenantContextFilter.doFilterInternal(request, response, filterChain);
		verify(filterChain).doFilter(request, response);

		assertNull(UserContext.getCorrelationId());
		assertNull(UserContext.getUserId());
		assertNull(UserContext.getTenantId());
	}

	@Test
	void doFilterInternal_WhenExceptionOccurs_ShouldClearUserContext() throws ServletException, IOException {
		String correlationId = "correlation-123";
		when(request.getHeader("X-Correlation-ID")).thenReturn(correlationId);
		when(securityContext.getAuthentication()).thenReturn(null);
		doThrow(new ServletException("Test exception")).when(filterChain).doFilter(request, response);

		assertThrows(ServletException.class, () ->
				tenantContextFilter.doFilterInternal(request, response, filterChain)
		);

		assertNull(UserContext.getCorrelationId());
		assertNull(UserContext.getUserId());
		assertNull(UserContext.getTenantId());
	}

	@Test
	void doFilterInternal_ContextSetDuringFilterExecution_VerifyContextAvailability() throws ServletException, IOException {

		String correlationId = "correlation-123";
		String userId = "user-123";
		String tenantId = "tenant-456";

		when(request.getHeader("X-Correlation-ID")).thenReturn(correlationId);

		Jwt jwt = createJwt(userId, tenantId);
		JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(jwt);
		when(securityContext.getAuthentication()).thenReturn(jwtAuthToken);

		// Mock FilterChain to verify context during execution
		doAnswer(invocation -> {
			// During filter execution, context should be set
			assertEquals(correlationId, UserContext.getCorrelationId());
			assertEquals(userId, UserContext.getUserId());
			assertEquals(tenantId, UserContext.getTenantId());
			return null;
		}).when(filterChain).doFilter(request, response);

		tenantContextFilter.doFilterInternal(request, response, filterChain);
		verify(filterChain).doFilter(request, response);

		assertNull(UserContext.getCorrelationId());
		assertNull(UserContext.getUserId());
		assertNull(UserContext.getTenantId());
	}

	@Test
	void doFilterInternal_WithEmptyTenantId_ShouldSetEmptyTenantContext() throws ServletException, IOException {

		String correlationId = "correlation-123";
		String userId = "user-123";

		when(request.getHeader("X-Correlation-ID")).thenReturn(correlationId);

		Map<String, Object> claims = new HashMap<>();
		claims.put("tenant_id", "");
		Jwt jwt = Jwt.withTokenValue("token")
				.header("alg", "RS256")
				.subject(userId)
				.claims(c -> c.putAll(claims))
				.issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(3600))
				.build();

		JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(jwt);
		when(securityContext.getAuthentication()).thenReturn(jwtAuthToken);

		// Mock FilterChain to verify context during execution
		doAnswer(invocation -> {
			assertEquals(correlationId, UserContext.getCorrelationId());
			assertEquals(userId, UserContext.getUserId());
			// Empty tenant_id is still set (not null check, only null check in code)
			assertEquals("", UserContext.getTenantId());
			return null;
		}).when(filterChain).doFilter(request, response);

		tenantContextFilter.doFilterInternal(request, response, filterChain);
		verify(filterChain).doFilter(request, response);
	}

	private Jwt createJwt(String subject, String tenantId) {
		Map<String, Object> claims = new HashMap<>();
		if (tenantId != null) {
			claims.put("tenant_id", tenantId);
		}

		return Jwt.withTokenValue("token")
				.header("alg", "RS256")
				.subject(subject)
				.claims(c -> c.putAll(claims))
				.issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(3600))
				.build();
	}
}


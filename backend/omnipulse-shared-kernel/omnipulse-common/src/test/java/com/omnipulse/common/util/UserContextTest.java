package com.omnipulse.common.util;

import org.junit.jupiter.api.*;
import org.slf4j.MDC;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserContext Tests")
class UserContextTest {

	@BeforeEach
	void setUp() {
		UserContext.clear();
		MDC.clear();
	}

	@AfterEach
	void tearDown() {
		UserContext.clear();
		MDC.clear();
	}

	@Nested
	@DisplayName("UserId Operations")
	class UserIdOperations {

		@Test
		@DisplayName("Should set and get userId successfully")
		void shouldSetAndGetUserId() {
			String userId = "user-123";
			UserContext.setUserId(userId);
			assertEquals(userId, UserContext.getUserId());
			assertEquals(userId, MDC.get(UserContext.KEY_USER));
		}

		@Test
		@DisplayName("Should remove userId when set to null")
		void shouldRemoveUserIdWhenNull() {
			UserContext.setUserId("user-123");
			UserContext.setUserId(null);
			assertNull(UserContext.getUserId());
			assertNull(MDC.get(UserContext.KEY_USER));
		}

		@Test
		@DisplayName("Should remove userId when set to empty string")
		void shouldRemoveUserIdWhenEmpty() {
			UserContext.setUserId("user-123");
			UserContext.setUserId("");
			assertNull(UserContext.getUserId());
			assertNull(MDC.get(UserContext.KEY_USER));
		}

		@Test
		@DisplayName("Should return null when userId is not set")
		void shouldReturnNullWhenUserIdNotSet() {
			assertNull(UserContext.getUserId());
		}
	}

	@Nested
	@DisplayName("TenantId Operations")
	class TenantIdOperations {

		@Test
		@DisplayName("Should set and get tenantId successfully")
		void shouldSetAndGetTenantId() {
			String tenantId = "tenant-456";
			UserContext.setTenantId(tenantId);
			assertEquals(tenantId, UserContext.getTenantId());
			assertEquals(tenantId, MDC.get(UserContext.KEY_TENANT));
		}

		@Test
		@DisplayName("Should remove tenantId when set to null")
		void shouldRemoveTenantIdWhenNull() {
			UserContext.setTenantId("tenant-456");
			UserContext.setTenantId(null);
			assertNull(UserContext.getTenantId());
			assertNull(MDC.get(UserContext.KEY_TENANT));
		}

		@Test
		@DisplayName("Should return null when tenantId is not set")
		void shouldReturnNullWhenTenantIdNotSet() {
			assertNull(UserContext.getTenantId());
		}
	}

	@Nested
	@DisplayName("CorrelationId Operations")
	class CorrelationIdOperations {

		@Test
		@DisplayName("Should set and get correlationId successfully")
		void shouldSetAndGetCorrelationId() {
			String correlationId = "corr-789";
			UserContext.setCorrelationId(correlationId);
			assertEquals(correlationId, UserContext.getCorrelationId());
			assertEquals(correlationId, MDC.get(UserContext.KEY_CORRELATION));
		}

		@Test
		@DisplayName("Should remove correlationId when set to null")
		void shouldRemoveCorrelationIdWhenNull() {
			UserContext.setCorrelationId("corr-789");
			UserContext.setCorrelationId(null);
			assertNull(UserContext.getCorrelationId());
			assertNull(MDC.get(UserContext.KEY_CORRELATION));
		}

		@Test
		@DisplayName("Should return null when correlationId is not set")
		void shouldReturnNullWhenCorrelationIdNotSet() {
			assertNull(UserContext.getCorrelationId());
		}
	}

	@Nested
	@DisplayName("Clear Operations")
	class ClearOperations {

		@Test
		@DisplayName("Should clear all context values")
		void shouldClearAllContextValues() {
			UserContext.setUserId("user-123");
			UserContext.setTenantId("tenant-456");
			UserContext.setCorrelationId("corr-789");

			UserContext.clear();

			assertNull(UserContext.getUserId());
			assertNull(UserContext.getTenantId());
			assertNull(UserContext.getCorrelationId());
			assertNull(MDC.get(UserContext.KEY_USER));
			assertNull(MDC.get(UserContext.KEY_TENANT));
			assertNull(MDC.get(UserContext.KEY_CORRELATION));
		}

		@Test
		@DisplayName("Should clear all MDC entries including custom ones")
		void shouldClearAllMDCEntries() {
			UserContext.setUserId("user-123");
			MDC.put("customKey", "customValue");

			UserContext.clear();

			assertNull(MDC.get("customKey"));
			assertNull(MDC.get(UserContext.KEY_USER));
		}

		@Test
		@DisplayName("Should not throw exception when clearing empty context")
		void shouldNotThrowExceptionWhenClearingEmptyContext() {
			assertDoesNotThrow(UserContext::clear);
		}
	}

	@Nested
	@DisplayName("Thread Safety Tests")
	class ThreadSafetyTests {

		@Test
		@DisplayName("Should maintain thread isolation for userId")
		void shouldMaintainThreadIsolationForUserId() throws InterruptedException {
			CountDownLatch latch = new CountDownLatch(2);
			AtomicReference<String> thread1UserId = new AtomicReference<>();
			AtomicReference<String> thread2UserId = new AtomicReference<>();

			Thread thread1 = new Thread(() -> {
				UserContext.setUserId("user-thread1");
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				thread1UserId.set(UserContext.getUserId());
				latch.countDown();
			});

			Thread thread2 = new Thread(() -> {
				UserContext.setUserId("user-thread2");
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				thread2UserId.set(UserContext.getUserId());
				latch.countDown();
			});

			thread1.start();
			thread2.start();
			assertTrue(latch.await(5, TimeUnit.SECONDS));

			assertEquals("user-thread1", thread1UserId.get());
			assertEquals("user-thread2", thread2UserId.get());
		}

		@Test
		@DisplayName("Should maintain thread isolation for all context values")
		void shouldMaintainThreadIsolationForAllValues() throws InterruptedException {

			int threadCount = 5;
			ExecutorService executor = Executors.newFixedThreadPool(threadCount);
			CountDownLatch latch = new CountDownLatch(threadCount);
			AtomicReference<Exception> exception = new AtomicReference<>();

			for (int i = 0; i < threadCount; i++) {
				final int threadId = i;
				executor.submit(() -> {
					try {
						String userId = "user-" + threadId;
						String tenantId = "tenant-" + threadId;
						String correlationId = "corr-" + threadId;

						UserContext.setUserId(userId);
						UserContext.setTenantId(tenantId);
						UserContext.setCorrelationId(correlationId);

						Thread.sleep(10);

						assertEquals(userId, UserContext.getUserId());
						assertEquals(tenantId, UserContext.getTenantId());
						assertEquals(correlationId, UserContext.getCorrelationId());
					} catch (Exception e) {
						exception.set(e);
					} finally {
						UserContext.clear();
						latch.countDown();
					}
				});
			}

			assertTrue(latch.await(5, TimeUnit.SECONDS));
			executor.shutdown();

			assertNull(exception.get(), "Thread safety test failed: " +
					(exception.get() != null ? exception.get().getMessage() : ""));
		}
	}

	@Nested
	@DisplayName("Integration Tests")
	class IntegrationTests {

		@Test
		@DisplayName("Should handle multiple set operations on same value")
		void shouldHandleMultipleSetOperations() {
			UserContext.setUserId("user-1");
			UserContext.setUserId("user-2");
			UserContext.setUserId("user-3");

			assertEquals("user-3", UserContext.getUserId());
			assertEquals("user-3", MDC.get(UserContext.KEY_USER));
		}

		@Test
		@DisplayName("Should handle set operations with whitespace")
		void shouldHandleSetOperationsWithWhitespace() {
			String userIdWithSpaces = "  user-123  ";
			UserContext.setUserId(userIdWithSpaces);
			assertEquals(userIdWithSpaces, UserContext.getUserId());
		}

		@Test
		@DisplayName("Should set all context values independently")
		void shouldSetAllContextValuesIndependently() {
			String userId = "user-123";
			String tenantId = "tenant-456";
			String correlationId = "corr-789";

			UserContext.setUserId(userId);
			UserContext.setTenantId(tenantId);
			UserContext.setCorrelationId(correlationId);

			assertEquals(userId, UserContext.getUserId());
			assertEquals(tenantId, UserContext.getTenantId());
			assertEquals(correlationId, UserContext.getCorrelationId());
			assertEquals(userId, MDC.get(UserContext.KEY_USER));
			assertEquals(tenantId, MDC.get(UserContext.KEY_TENANT));
			assertEquals(correlationId, MDC.get(UserContext.KEY_CORRELATION));
		}

		@Test
		@DisplayName("Should clear specific values without affecting others")
		void shouldClearSpecificValuesWithoutAffectingOthers() {
			UserContext.setUserId("user-123");
			UserContext.setTenantId("tenant-456");
			UserContext.setCorrelationId("corr-789");

			UserContext.setUserId(null);

			assertNull(UserContext.getUserId());
			assertEquals("tenant-456", UserContext.getTenantId());
			assertEquals("corr-789", UserContext.getCorrelationId());
		}

		@Test
		@DisplayName("Should handle special characters in values")
		void shouldHandleSpecialCharactersInValues() {
			String specialUserId = "user-123@#$%^&*()_+-=[]{}|;:',.<>?/~`";
			UserContext.setUserId(specialUserId);
			assertEquals(specialUserId, UserContext.getUserId());
			assertEquals(specialUserId, MDC.get(UserContext.KEY_USER));
		}

		@Test
		@DisplayName("Should verify constant key values")
		void shouldVerifyConstantKeyValues() {
			assertEquals("userId", UserContext.KEY_USER);
			assertEquals("tenantId", UserContext.KEY_TENANT);
			assertEquals("correlationId", UserContext.KEY_CORRELATION);
		}
	}
}


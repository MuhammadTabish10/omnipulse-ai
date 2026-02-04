package com.omnipulse.identity.controller;

import com.omnipulse.common.dto.response.ApiResponse;
import com.omnipulse.common.dto.response.PagedResponse;
import com.omnipulse.identity.dto.UserRequest;
import com.omnipulse.identity.dto.UserResponse;
import com.omnipulse.identity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User Management", description = "Operations for managing user profiles and identity")
public class UserController {

	private final UserService userService;

	@PostMapping
	@Operation(summary = "Register User", description = "Creates a new profile linked to the logged-in Keycloak account.")
	public ResponseEntity<ApiResponse<UserResponse>> registerUser(@RequestBody @Valid UserRequest request,
	                                                              @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
		String externalId = jwt.getClaimAsString("sub");
		UserResponse userResponse = userService.createUser(request, externalId);
		return ResponseEntity.ok(ApiResponse.success(userResponse, "User registered successfully."));
	}

	@GetMapping("/me")
	@Operation(summary = "Get My Profile", description = "Retrieves the profile of the currently logged-in user.")
	public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
		String externalId = jwt.getClaimAsString("sub");
		UserResponse userResponse = userService.findUserByExternalId(externalId);
		return ResponseEntity.ok(ApiResponse.success(userResponse, "User found successfully."));
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get All Users", description = "Get a paginated list of all users.")
	public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUSers(@Parameter(hidden = true)
	                                                                            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
		Page<UserResponse> userPage = userService.getAllUsers(pageable);
		return ResponseEntity.ok(ApiResponse.successPaged(userPage));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get User by ID", description = "Retrieves a user by their internal UUID.")
	public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
		UserResponse userResponse = userService.findUserById(id);
		return ResponseEntity.ok(ApiResponse.success(userResponse, "User found successfully."));
	}

	@GetMapping("/search")
	@Operation(summary = "Find User by Email")
	public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@RequestParam String email) {
		UserResponse response = userService.findUserByEmail(email);
		return ResponseEntity.ok(ApiResponse.success(response, "User retrieved successfully"));
	}

	@PatchMapping("/{id}/status")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Enable/Disable User", description = "Admin operation to ban or reactivate a user.")
	public ResponseEntity<ApiResponse<UserResponse>> toggleUserStatus(@PathVariable UUID id, @RequestParam boolean active) {
		UserResponse response = userService.toggleUserStatus(id, active);
		String statusMsg = active ? "activated" : "disabled";
		return ResponseEntity.ok(ApiResponse.success(response, "User account " + statusMsg));
	}
}

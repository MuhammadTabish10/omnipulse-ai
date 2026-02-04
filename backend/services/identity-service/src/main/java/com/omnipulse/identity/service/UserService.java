package com.omnipulse.identity.service;

import com.omnipulse.identity.dto.UserRequest;
import com.omnipulse.identity.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
	UserResponse createUser(UserRequest request, String externalId);
	UserResponse findUserById(UUID id);
	UserResponse findUserByExternalId(String id);
	UserResponse findUserByEmail(String email);
	Page<UserResponse> getAllUsers(Pageable pageable);
	UserResponse toggleUserStatus(UUID id, boolean isActive);
}

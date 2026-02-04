package com.omnipulse.identity.service.impl;

import com.omnipulse.common.enums.ApiResponseCode;
import com.omnipulse.common.exception.DuplicateResourceException;
import com.omnipulse.common.exception.ResourceNotFoundException;
import com.omnipulse.identity.domain.User;
import com.omnipulse.identity.dto.UserRequest;
import com.omnipulse.identity.dto.UserResponse;
import com.omnipulse.identity.mapper.UserMapper;
import com.omnipulse.identity.repository.UserRepository;
import com.omnipulse.identity.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserMapper userMapper;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public UserResponse createUser(UserRequest request, String externalId) {
		log.debug("Creating user profile for email: {}, externalId: {}", request.email(), externalId);

		validateUserUniqueness(request.email(), externalId);

		User user = userMapper.toEntity(request);
		user.setExternalId(externalId);
		User savedUser = userRepository.save(user);

		log.info("User profile created successfully - internalId: {}, email: {}", savedUser.getId(), savedUser.getEmail());
		return userMapper.toResponse(savedUser);
	}

	@Override
	public UserResponse findUserById(UUID id) {
		return userRepository.findById(id)
				.map(userMapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
	}

	@Override
	public UserResponse findUserByExternalId(String id) {
		return userRepository.findByExternalId(id)
				.map(userMapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("User", "externalId", id));
	}

	@Override
	public UserResponse findUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.map(userMapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
	}

	@Override
	public Page<UserResponse> getAllUsers(Pageable pageable) {
		return userRepository.findAll(pageable)
				.map(userMapper::toResponse);
	}

	@Override
	@Transactional
	public UserResponse toggleUserStatus(UUID id, boolean isActive) {
		log.debug("Attempting to change user {} status to active={}", id, isActive);

		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

		if (user.isActive() == isActive) {
			log.debug("User {} already has active={}, skipping update", id, isActive);
			return userMapper.toResponse(user);
		}

		boolean previousStatus = user.isActive();
		user.setActive(isActive);
		User updatedUser = userRepository.save(user);

		log.info("User status updated successfully - id: {}, email: {}, previousStatus: {}, newStatus: {}",
				id, user.getEmail(), previousStatus, isActive);

		return userMapper.toResponse(updatedUser);
	}

	private void validateUserUniqueness(String email, String externalId) {
		if (userRepository.existsByEmail(email)) {
			log.warn("Attempt to create user with duplicate email: {}", email);
			throw new DuplicateResourceException(
					String.format("User with email %s already exists", email),
					ApiResponseCode.EMAIL_ALREADY_EXISTS
			);
		}

		if (userRepository.findByExternalId(externalId).isPresent()) {
			log.warn("Attempt to create user with duplicate externalId: {}", externalId);
			throw new DuplicateResourceException(
					String.format("User with external ID %s already exists", externalId),
					ApiResponseCode.USER_ALREADY_EXISTS
			);
		}
	}
}

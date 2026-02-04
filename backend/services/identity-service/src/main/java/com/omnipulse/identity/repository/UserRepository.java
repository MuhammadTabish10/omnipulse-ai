package com.omnipulse.identity.repository;

import com.omnipulse.identity.domain.User;
import com.omnipulse.identity.dto.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	boolean existsByEmail(String email);
	Optional<User> findByExternalId(String externalId);
	Optional<User> findByEmail(String email);
}

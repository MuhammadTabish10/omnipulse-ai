package com.omnipulse.identity.domain;

import com.omnipulse.jpa.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
@Table(name = "users")
public class User extends BaseEntity {

	@Column(name = "external_id", unique = true, nullable = false)
	private String externalId;

	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	@Column(name = "tenant_id", nullable = false)
	private String tenantId;

	@Column(name = "is_active")
	private boolean isActive = true;
}

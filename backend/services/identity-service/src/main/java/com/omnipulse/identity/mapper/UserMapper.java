package com.omnipulse.identity.mapper;

import com.omnipulse.identity.domain.User;
import com.omnipulse.identity.dto.UserRequest;
import com.omnipulse.identity.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "externalId", ignore = true)
	@Mapping(target = "active", constant = "true")
	User toEntity(UserRequest userRequest);

	@Mapping(source = "active", target = "isActive")
	UserResponse toResponse(User user);
}

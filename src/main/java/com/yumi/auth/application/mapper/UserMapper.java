package com.yumi.auth.application.mapper;

import com.yumi.auth.application.dto.RegisterRequest;
import com.yumi.auth.application.dto.UserResponse;
import com.yumi.auth.domain.User;
import com.yumi.auth.domain.UserRole;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Transformaciones entre modelo y DTO.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

  public static User toEntity(RegisterRequest req, String sessionId, String encodedPwd) {
    return User.builder()
        .email(req.email())
        .password(encodedPwd)
        .fullName(req.fullName())
        .role(UserRole.CUSTOMER)
        .sessionId(sessionId)
        .build();
  }

  public static UserResponse toResponse(User u) {
    return new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getRole());
  }
}
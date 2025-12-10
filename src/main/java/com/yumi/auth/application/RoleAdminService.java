package com.yumi.auth.application;

import com.yumi.auth.application.dto.ChangeRoleRequest;
import com.yumi.auth.application.dto.UserResponse;
import com.yumi.auth.application.mapper.UserMapper;
import com.yumi.auth.domain.User;
import com.yumi.auth.infrastructure.persistence.JpaUserRepository;
import com.yumi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Casos de uso de administración de roles.
 */
@Service
@RequiredArgsConstructor
public class RoleAdminService {

  private final JpaUserRepository userRepository;

  public Page<UserResponse> listAll(Pageable pageable) {
    return userRepository.findAll(pageable)
        .map(UserMapper::toResponse);
  }

  @Transactional
  public UserResponse changeRole(Long userId, ChangeRoleRequest req) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    user.setRole(req.role());
    return UserMapper.toResponse(user);
  }
}
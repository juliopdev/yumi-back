package com.yumi.auth.application;

import com.yumi.auth.application.dto.UserEditRequest;
import com.yumi.auth.application.dto.UserResponse;
import com.yumi.auth.application.mapper.UserMapper;
import com.yumi.auth.domain.User;
import com.yumi.auth.infrastructure.persistence.JpaUserRepository;
import com.yumi.auth.infrastructure.security.AuthContext;
import com.yumi.shared.exception.DuplicateResourceException;
import com.yumi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Casos de uso del perfil del usuario autenticado.
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

  private final JpaUserRepository userRepository;
  private final AuthContext authContext;

  public UserResponse getMyProfile() {
    return authContext.currentUser()
        .map(UserMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
  }

  @Transactional
  public UserResponse updateProfile(UserEditRequest req) {
    User me = authContext.currentUser()
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    if (!me.getEmail().equals(req.newEmail()) && userRepository.existsByEmail(req.newEmail())) {
      throw new DuplicateResourceException("Email ya en uso");
    }
    me.setEmail(req.newEmail());
    me.setFullName(req.newFullName());
    return UserMapper.toResponse(me);
  }
}
package com.yumi.auth.application;

import com.yumi.auth.application.dto.*;
import com.yumi.auth.application.mapper.UserMapper;
import com.yumi.auth.domain.User;
import com.yumi.auth.infrastructure.persistence.JpaUserRepository;
import com.yumi.auth.infrastructure.security.AuthContext;
import com.yumi.shared.exception.BadCredentialsException;
import com.yumi.shared.exception.DuplicateResourceException;
import com.yumi.shared.exception.ResourceNotFoundException;
import com.yumi.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Casos de uso de autenticación y registro.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

  private final JpaUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthContext authContext;
  private final SessionMigrationService migrationService;

  @Transactional
  public AuthResponse register(RegisterRequest req) {
    if (userRepository.existsByEmail(req.email())) {
      throw new DuplicateResourceException("Email ya registrado");
    }
    User user = UserMapper.toEntity(req,
        authContext.currentSessionId(),
        passwordEncoder.encode(req.password()));
    System.out.println("user\n" +user);
    userRepository.save(user);
    migrationService.migrateCart(authContext.currentSessionId(), user);
    return new AuthResponse(generateToken(user), UserMapper.toResponse(user));
  }

  public AuthResponse login(LoginRequest req) {
    User user = userRepository.findByEmail(req.email())
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    if (!passwordEncoder.matches(req.password(), user.getPassword())) {
      throw new BadCredentialsException("Contraseña incorrecta");
    }
    return new AuthResponse(generateToken(user), UserMapper.toResponse(user));
  }

  public AuthResponse changePassword(ChangePasswordRequest req) {
    User user = authContext.currentUser()
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {
      throw new BadCredentialsException("Contraseña incorrecta");
    }
    if (req.oldPassword().equals(req.newPassword())) {
      throw new BadCredentialsException("Las contraseñas no pueden ser iguales");
    }
    user.setPassword(passwordEncoder.encode(req.newPassword()));
    userRepository.save(user);
    return new AuthResponse(generateToken(user), UserMapper.toResponse(user));
  }

  /* ---------- privates ---------- */
  private String generateToken(User u) {
    return jwtService.generateToken(u.getEmail(), u.getSessionId(), u.getRole().name());
  }
}
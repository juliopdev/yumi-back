package com.yumi.auth.application;

import com.yumi.auth.domain.User;
import com.yumi.cart.infrastructure.persistence.JpaCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SessionMigrationService {

  private final JpaCartRepository cartRepository;

  @Transactional
  public void migrateCart(String oldSessionId, User newUser) {
    cartRepository.findBySessionId(oldSessionId).ifPresent(cart -> {
      cart.migrateToUser(newUser.getEmail());
      cartRepository.save(cart);
    });
  }
}
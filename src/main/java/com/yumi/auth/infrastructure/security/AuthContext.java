package com.yumi.auth.infrastructure.security;

import com.yumi.auth.domain.User;
import com.yumi.auth.infrastructure.persistence.JpaUserRepository;
import com.yumi.cart.domain.Cart;
import com.yumi.cart.infrastructure.persistence.JpaCartRepository;
import com.yumi.shared.security.SessionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utilidad para obtener el usuario autenticado.
 */
@Component
@RequiredArgsConstructor
public class AuthContext {

  private final JpaUserRepository userRepository;
  private final JpaCartRepository cartRepository;
  private final SessionContext sessionContext;

  /* --------- sessionId ÚNICA fuente de verdad --------- */
  public String currentSessionId() {
    return sessionContext.getSessionId();
  }

  /* --------- usuario autenticado (si lo hay) --------- */
  public Optional<User> currentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || "anonymous".equals(auth.getName()))
      return Optional.empty();
    return userRepository.findByEmail(auth.getName());
  }

  /* --------- carrito del usuario autenticado --------- */
  public Optional<Cart> currentCart() {
    return currentUser().flatMap(u -> cartRepository.findByOwnerEmail(u.getEmail()));
  }
}
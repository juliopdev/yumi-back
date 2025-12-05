package com.yumi.auth.infrastructure.persistence;

import com.yumi.auth.domain.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);

  boolean existsBySessionId(String sessionId);

  Optional<User> findByEmail(String email);

  Optional<User> findBySessionId(String sessionId);
}
package com.yumi.cart.infrastructure.persistence;

import com.yumi.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JpaCartRepository extends JpaRepository<Cart, Long> {

  Optional<Cart> findByOwnerEmail(String ownerEmail);

  @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.ownerEmail = :email")
  Optional<Cart> findByOwnerEmailWithItems(@Param("email") String email);

  Optional<Cart> findBySessionId(String sessionId);

  @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.sessionId = :sid")
  Optional<Cart> findBySessionIdWithItems(@Param("sid") String sessionId);
}
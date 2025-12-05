package com.yumi.cart.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Entity
@Table(
  name = "carts",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_cart_owner", columnNames = {"owner_email"}),
    @UniqueConstraint(name = "uk_cart_session", columnNames = {"session_id"})
  }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = true)
  private String sessionId;

  @Column(unique = false, nullable = false)
  private String ownerEmail;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<CartItem> items = new ArrayList<>();

  @Column(nullable = false)
  private Instant createdAt;

  @PrePersist
  void prePersist() {
    this.createdAt = Instant.now();
  }

  /* helpers */
  public void clearItems() {
    items.clear();
  }

  public void addItem(CartItem item) {
    items.add(item);
  }

  public void migrateToUser(String email) {
    this.sessionId = null;
    this.ownerEmail = email;
  }

  public Optional<CartItem> getItemBySku(String sku) {
    return items.stream()
        .filter(i -> i.getProductSku().equals(sku))
        .findFirst();
  }
}
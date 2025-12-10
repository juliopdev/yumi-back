package com.yumi.order.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.yumi.address.domain.Address;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, updatable = false)
  private String sku;

  @Column(nullable = false)
  private String customerEmail;

  @Column(nullable = false)
  private String customerFullName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "address_id", nullable = false)
  private Address address;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<OrderItem> items = new ArrayList<>();

  @Column(nullable = false)
  private BigDecimal total;

  @CreatedDate
  private Instant createdAt;

  /* helpers */
  public void addItem(OrderItem item) {
    items.add(item);
    item.setOrder(this);
  }

  public void setTotal(BigDecimal total) {
    this.total = total;
  }

  public void changeStatus(OrderStatus newStatus) {
    OrderStatusTransition.validate(this.status, newStatus);
    this.status = newStatus;
  }
}
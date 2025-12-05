package com.yumi.order.domain;

/**
 * Reglas de transición de estado.
 */
public enum OrderStatusTransition {
  PENDING_TO_APPROVED,
  PENDING_TO_REJECTED,
  APPROVED_TO_SHIPPED,
  SHIPPED_TO_DELIVERED,
  ANY_TO_CANCELLED;

  public static void validate(OrderStatus from, OrderStatus to) {
    if (from == to)
      return;
    switch (from) {
      case PENDING -> {
        if (to != OrderStatus.APPROVED && to != OrderStatus.REJECTED && to != OrderStatus.CANCELLED)
          throw new IllegalArgumentException("Transición no permitida desde PENDING");
      }
      case APPROVED -> {
        if (to != OrderStatus.SHIPPED && to != OrderStatus.CANCELLED)
          throw new IllegalArgumentException("Transición no permitida desde APPROVED");
      }
      case SHIPPED -> {
        if (to != OrderStatus.DELIVERED)
          throw new IllegalArgumentException("Transición no permitida desde SHIPPED");
      }
      case DELIVERED, REJECTED, CANCELLED -> throw new IllegalArgumentException("Estado final");
    }
  }
}
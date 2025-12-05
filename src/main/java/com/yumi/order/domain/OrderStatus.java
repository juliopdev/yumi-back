package com.yumi.order.domain;

public enum OrderStatus {
  PENDING,
  APPROVED, // 80 % éxito
  REJECTED, // 20 % fracaso
  SHIPPED,
  DELIVERED,
  CANCELLED
}
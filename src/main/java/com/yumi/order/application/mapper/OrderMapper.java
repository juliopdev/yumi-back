package com.yumi.order.application.mapper;

import com.yumi.address.application.mapper.AddressMapper;
import com.yumi.address.domain.Address;
import com.yumi.auth.domain.User;
import com.yumi.order.application.dto.*;
import com.yumi.order.domain.Order;
import com.yumi.order.domain.OrderStatus;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Transformaciones entre Order y sus DTOs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderMapper {

  public static Order toEntity(CreateOrderRequest req, UserInOrderResponse customerDetail, Address address) {
    return Order.builder()
        .customerEmail(customerDetail.email())
        .customerFullName(customerDetail.fullName())
        .address(address)
        .status(OrderStatus.PENDING)
        .total(BigDecimal.ZERO)
        .build();
  }

  public static OrderResponse toResponse(Order o) {
    List<OrderItemResponse> items = o.getItems().stream()
        .map(OrderItemMapper::toResponse)
        .toList();

    return new OrderResponse(
        o.getId(),
        o.getSku(),
        o.getCustomerEmail(),
        o.getStatus(),
        AddressMapper.toResponse(o.getAddress()),
        items,
        o.getTotal(),
        o.getCreatedAt());
  }

  public static UserInOrderResponse toUserInOrderResponse(User u) {
    return new UserInOrderResponse(
        u.getId(),
        u.getEmail(),
        u.getFullName());
  }
  public static UserInOrderResponse toUserInOrderResponse(Long id, String email, String fullName) {
    return new UserInOrderResponse(id, email, fullName);
  }
}
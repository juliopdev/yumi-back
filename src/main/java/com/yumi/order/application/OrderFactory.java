package com.yumi.order.application;

import com.yumi.address.domain.Address;
import com.yumi.order.application.dto.CreateOrderRequest;
import com.yumi.order.application.dto.UserInOrderResponse;
import com.yumi.order.application.mapper.OrderItemMapper;
import com.yumi.order.domain.Order;
import com.yumi.order.domain.OrderItem;
import com.yumi.order.domain.OrderStatus;
import com.yumi.catalog.domain.Product;
import com.yumi.catalog.infrastructure.persistence.JpaProductRepository;
import com.yumi.shared.exception.BadRequestException;
import com.yumi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFactory {

  private final JpaProductRepository productRepository;
  private final OrderNumberGenerator numberGenerator;

  public Order create(CreateOrderRequest req, UserInOrderResponse customer, Address address) {
    Order order = Order.builder()
        .sku(numberGenerator.next())
        .customerEmail(customer.email())
        .customerFullName(customer.fullName())
        .status(OrderStatus.PENDING)
        .address(address)
        .build();

    List<OrderItem> items = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;

    for (var ir : req.items()) {
      Product p = productRepository.findByVisibleTrueAndSku(ir.productSku())
          .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

      if (p.getStock() < ir.quantity()) {
        throw new BadRequestException("Stock insuficiente: " + p.getName());
      }
      OrderItem oi = OrderItemMapper.toEntity(ir, p);
      items.add(oi);
      total = total.add(oi.getSubtotal());

      p.setStock(p.getStock() - ir.quantity());
      productRepository.save(p);
    }
    items.forEach(order::addItem);
    order.setTotal(total);
    return order;
  }
}
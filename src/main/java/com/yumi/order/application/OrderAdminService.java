package com.yumi.order.application;

import com.yumi.order.application.dto.OrderResponse;
import com.yumi.order.application.dto.OrderStatusRequest;
import com.yumi.order.application.mapper.OrderMapper;
import com.yumi.order.domain.Order;
import com.yumi.order.infrastructure.persistence.JpaOrderRepository;
import com.yumi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderAdminService {

  private final JpaOrderRepository repository;

  public Page<OrderResponse> listAll(Pageable pageable) {
    return repository.findAllWithItemsAndAddress(pageable)
        .map(OrderMapper::toResponse);
  }

  @Transactional
  public OrderResponse changeStatus(String sku, OrderStatusRequest req) {
    Order order = repository.findBySku(sku)
        .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
    order.changeStatus(req.status());
    return OrderMapper.toResponse(order);
  }
}
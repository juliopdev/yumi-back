package com.yumi.order.application;

import com.yumi.address.application.mapper.AddressMapper;
import com.yumi.address.domain.Address;
import com.yumi.address.infrastructure.persistence.JpaAddressRepository;
import com.yumi.auth.infrastructure.security.AuthContext;
import com.yumi.cart.infrastructure.persistence.JpaCartRepository;
import com.yumi.order.application.dto.*;
import com.yumi.order.application.mapper.OrderMapper;
import com.yumi.order.domain.Order;
import com.yumi.order.infrastructure.persistence.JpaOrderRepository;
import com.yumi.shared.exception.BadRequestException;
import com.yumi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

  private final JpaOrderRepository orderRepository;
  private final JpaAddressRepository addressRepository;
  private final JpaCartRepository cartRepository;
  private final OrderFactory orderFactory;
  private final AuthContext authContext;

  /* ------------- QUERIES ------------- */

  public Page<OrderResponse> myOrders(Pageable pageable) {
    String email = authContext.currentUser()
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"))
        .getEmail();
    Page<OrderResponse> res = orderRepository.findByCustomerEmailOrderByCreatedAtDesc(email, pageable)
        .map(OrderMapper::toResponse);
    return res;
  }

  public OrderResponse getMyOrder(String sku) {
    return getMyOrderToChat(sku)
        .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
  }

  public Optional<OrderResponse> getMyOrderToChat(String sku) {
    String email = authContext.currentUser()
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"))
        .getEmail();
    return orderRepository.findBySkuAndCustomerEmail(sku, email)
        .map(OrderMapper::toResponse);
  }

  @Transactional
  public OrderResponse createOrder(CreateOrderRequest req) {
    validateAuthenticatedRequest(req);

    Address address = getOrCreateAddress(req);
    UserInOrderResponse customer = OrderMapper.toUserInOrderResponse(
        authContext.currentUser().get());
    Order order = orderFactory.create(req, customer, address);
    cartRepository.findByOwnerEmailWithItems(customer.email())
        .ifPresent(cart -> {
          cart.clearItems();
          cartRepository.save(cart);
        });
    orderRepository.save(order);
    return OrderMapper.toResponse(order);
  }

  /* ---------- público para anónimos ---------- */
  @Transactional
  public OrderResponse createAnonymousOrder(String sessionId, CreateOrderRequest req) {
    if (!req.isAnonymous()) {
      throw new BadRequestException("isAnonymous debe ser true");
    }
    Address address = AddressMapper.toEntity(req.addressDetail(), "anonymous");
    address = addressRepository.save(address);

    UserInOrderResponse customer = new UserInOrderResponse(null, "anonymous", "Invitado");
    Order order = orderFactory.create(req, customer, address);

    cartRepository.findBySessionIdWithItems(sessionId)
        .ifPresent(cart -> {
          cart.clearItems();
          cartRepository.save(cart);
        });
    return OrderMapper.toResponse(order);
  }

  /* ---------- helpers ---------- */
  private void validateAuthenticatedRequest(CreateOrderRequest req) {
    if (req.isAnonymous()) {
      throw new BadRequestException("isAnonymous debe ser false para usuarios autenticados");
    }
    String jwtEmail = authContext.currentUser().get().getEmail();
    if (!jwtEmail.equalsIgnoreCase(req.customerEmail())) {
      throw new BadRequestException("El email del JWT no coincide con customerEmail");
    }
  }

  private Address getOrCreateAddress(CreateOrderRequest req) {
    if (req.addressDetail().id() != null) {
      return addressRepository
          .findByIdAndOwnerEmail(req.addressDetail().id(), req.customerEmail())
          .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada"));
    }
    return addressRepository.save(
        AddressMapper.toEntity(req.addressDetail(), req.customerEmail()));
  }
}
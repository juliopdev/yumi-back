package com.yumi.cart.application.mapper;

import com.yumi.cart.application.dto.CartItemResponse;
import com.yumi.cart.application.dto.CartResponse;
import com.yumi.cart.application.dto.ProductSnapshot;
import com.yumi.cart.domain.Cart;
import com.yumi.cart.domain.CartItem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Transformaciones entre modelo y DTO.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CartMapper {
  private static final BigDecimal ONE_PLUS_IGV = new BigDecimal("1.18");

  public static CartResponse toResponse(Cart cart, List<ProductSnapshot> snapshots) {
    Map<String, ProductSnapshot> bySku = snapshots.stream()
        .collect(Collectors.toMap(ProductSnapshot::sku, Function.identity()));
    List<CartItemResponse> items = cart.getItems().stream()
        .map(i -> toItemResponse(i, bySku.get(i.getProductSku())))
        .toList();
    BigDecimal totalConIGV = items.stream()
        .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal baseImponible = totalConIGV.divide(ONE_PLUS_IGV, 2, RoundingMode.HALF_UP);
    BigDecimal igv = totalConIGV.subtract(baseImponible);
    BigDecimal igv_rate = ONE_PLUS_IGV;

    return new CartResponse(
        cart.getId(),
        cart.getOwnerEmail(),
        items,
        baseImponible,
        igv,
        igv_rate,
        totalConIGV);
  }

  private static CartItemResponse toItemResponse(CartItem item, ProductSnapshot snap) {
    return new CartItemResponse(
        item.getId(),
        item.getProductSku(),
        snap != null ? snap.name() : "Producto no disponible",
        snap != null ? snap.unitPrice() : BigDecimal.ZERO,
        snap != null ? snap.imageUrl() : "",
        item.getQuantity());
  }
}
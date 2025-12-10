package com.yumi.cart.application;

import com.yumi.cart.application.dto.AddItemRequest;
import com.yumi.cart.application.dto.CartResponse;
import com.yumi.cart.application.dto.ProductSnapshot;
import com.yumi.cart.application.mapper.CartMapper;
import com.yumi.cart.domain.Cart;
import com.yumi.cart.domain.CartItem;
import com.yumi.cart.infrastructure.persistence.JpaCartRepository;
import com.yumi.auth.infrastructure.security.AuthContext;
import com.yumi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Casos de uso del carrito (tanto autenticado como anónimo).
 */
@Service
@RequiredArgsConstructor
public class CartService {

  private final JpaCartRepository cartRepository;
  private final AuthContext authContext;
  private final ProductCatalogReader catalogReader;

  /* -------------------- AUTHENTICATED -------------------- */
  public CartResponse getMyCart() {
    return enrich(getOrCreateCart());
  }

  @Transactional
  public CartResponse mergeMyCart(List<AddItemRequest> items) {
    Cart cart = getOrCreateCart();
    items.forEach(req -> addItemToCart(cart, req));
    return enrich(cart);
  }

  @Transactional
  public CartResponse addItem(AddItemRequest req) {
    Cart cart = getOrCreateCart();
    addItemToCart(cart, req);
    return enrich(cart);
  }

  @Transactional
  public CartResponse updateItemQuantityAuthenticated(Long itemId, Integer newQuantity) {
    Cart cart = getOrCreateCart();
    CartItem item = cart.getItems().stream()
        .filter(i -> i.getId().equals(itemId))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));
    item.setQuantity(newQuantity);
    return enrich(cart);
  }

  @Transactional
  public CartResponse removeItem(Long itemId) {
    Cart cart = getOrCreateCart();
    boolean removed = cart.getItems().removeIf(i -> i.getId().equals(itemId));
    if (!removed)
      throw new ResourceNotFoundException("Item no encontrado");
    return enrich(cart);
  }

  @Transactional
  public void clearMyCart() {
    getOrCreateCart().clearItems();
  }

  /* -------------------- ANONYMOUS -------------------- */
  @Transactional
  public CartResponse getAnonymousCart(String sessionId) {
    return enrich(getOrCreateAnonymousCart(sessionId));
  }

  @Transactional
  public CartResponse addAnonymousItem(String sessionId, AddItemRequest req) {
    Cart cart = getOrCreateAnonymousCart(sessionId);
    addItemToCart(cart, req);
    return enrich(cart);
  }

  @Transactional
  public CartResponse updateItemQuantityAnonymous(String sessionId, Long itemId, Integer newQuantity) {
    Cart cart = getOrCreateAnonymousCart(sessionId);
    CartItem item = cart.getItems().stream()
        .filter(i -> i.getId().equals(itemId))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));
    item.setQuantity(newQuantity);
    return enrich(cart);
  }

  @Transactional
  public CartResponse removeAnonymousItem(String sessionId, Long itemId) {
    Cart cart = getOrCreateAnonymousCart(sessionId);
    boolean removed = cart.getItems().removeIf(i -> i.getId().equals(itemId));
    if (!removed)
      throw new ResourceNotFoundException("Item no encontrado");
    return enrich(cart);
  }

  @Transactional
  public void clearAnonymousCart(String sessionId) {
    Cart cart = getOrCreateAnonymousCart(sessionId);
    cart.clearItems();
    cartRepository.save(cart);
  }

  /* -------------------- HELPERS -------------------- */
  private Cart getOrCreateCart() {
    String email = authContext.currentUser()
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"))
        .getEmail();
    return cartRepository.findByOwnerEmailWithItems(email)
        .orElseGet(() -> cartRepository.save(Cart.builder().ownerEmail(email).build()));
  }

  private Cart getOrCreateAnonymousCart(String sessionId) {
    return cartRepository.findBySessionIdWithItems(sessionId)
        .orElseGet(() -> cartRepository.save(
            Cart.builder()
                .sessionId(sessionId)
                .ownerEmail("anonymous"+sessionId)
                .build()));
  }

  private void addItemToCart(Cart cart, AddItemRequest req) {
    CartItem item = cart.getItemBySku(req.productSku())
        .orElseGet(() -> {
          ProductSnapshot snap = catalogReader.snapshotsBySkus(Set.of(req.productSku()))
              .stream()
              .findFirst()
              .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + req.productSku()));

          return CartItem.builder()
              .productSku(req.productSku())
              .productName(snap.name())
              .imageUrl(snap.imageUrl())
              .unitPrice(snap.unitPrice())
              .quantity(0)
              .cart(cart)
              .build();
        });

    item.setQuantity(item.getQuantity() + req.quantity());
    if (item.getId() == null) {
      cart.addItem(item);
    }
    cartRepository.save(cart);
  }

  private CartResponse enrich(Cart cart) {
    Set<String> skus = cart.getItems().stream()
        .map(CartItem::getProductSku)
        .collect(Collectors.toSet());
    return CartMapper.toResponse(cart, catalogReader.snapshotsBySkus(skus));
  }
}
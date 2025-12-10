package com.yumi.cart.infrastructure.web;

import com.yumi.cart.application.CartService;
import com.yumi.cart.application.dto.AddItemRequest;
import com.yumi.cart.application.dto.CartResponse;
import com.yumi.shared.util.ApiResponse;
import com.yumi.auth.infrastructure.security.AuthContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints del carrito (autenticado o anónimo).
 */
@RestController
@RequestMapping(CartController.BASE_PATH)
@RequiredArgsConstructor
public class CartController {

  public static final String BASE_PATH = "/api/cart";
  private final CartService cartService;
  private final AuthContext authContext;

  private boolean isAuthenticated() {
    return authContext.currentUser().isPresent();
  }

  private String sessionId(HttpServletRequest req) {
    return req.getHeader("X-Session-Id");
  }

  @GetMapping
  public ApiResponse<CartResponse> getCart(HttpServletRequest request) {
    CartResponse body = isAuthenticated()
        ? cartService.getMyCart()
        : cartService.getAnonymousCart(sessionId(request));
    return ApiResponse.ok(body, request);
  }

  @PostMapping("/merge")
  public ApiResponse<CartResponse> mergeCart(
      @Valid @RequestBody List<AddItemRequest> items,
      HttpServletRequest request) {
    CartResponse body = cartService.mergeMyCart(items);
    return ApiResponse.ok(body, request);
  }

  @PostMapping("/items")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<CartResponse> addItem(
      @Valid @RequestBody AddItemRequest req,
      HttpServletRequest request) {
    System.out.println("addItem:\n");
    System.out.println(req);
    CartResponse body = isAuthenticated()
        ? cartService.addItem(req)
        : cartService.addAnonymousItem(sessionId(request), req);
    return ApiResponse.ok(body, request);
  }

  @PatchMapping("/items/{itemId}")
  public ApiResponse<CartResponse> updateItemQuantity(
      @PathVariable Long itemId,
      @RequestParam Integer quantity,
      HttpServletRequest request) {

    CartResponse body = isAuthenticated()
        ? cartService.updateItemQuantityAuthenticated(itemId, quantity)
        : cartService.updateItemQuantityAnonymous(sessionId(request), itemId, quantity);

    return ApiResponse.ok(body, request);
  }

  @DeleteMapping("/items/{itemId}")
  public ApiResponse<CartResponse> removeItem(
      @PathVariable Long itemId,
      HttpServletRequest request) {
    CartResponse body = isAuthenticated()
        ? cartService.removeItem(itemId)
        : cartService.removeAnonymousItem(sessionId(request), itemId);
    return ApiResponse.ok(body, request);
  }

  @DeleteMapping
  public ApiResponse<String> clearCart(HttpServletRequest request) {
    if (isAuthenticated()) cartService.clearMyCart();
    else cartService.clearAnonymousCart(sessionId(request));
    return ApiResponse.ok("Carrito vacío", request);
  }
}
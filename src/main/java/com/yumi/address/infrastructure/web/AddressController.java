package com.yumi.address.infrastructure.web;

import com.yumi.address.application.AddressService;
import com.yumi.address.application.dto.AddressRequest;
import com.yumi.address.application.dto.AddressResponse;
import com.yumi.shared.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AddressController.BASE_PATH)
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AddressController {

  public static final String BASE_PATH = "/api/addresses";
  private final AddressService service;

  @GetMapping
  public ApiResponse<List<AddressResponse>> list(HttpServletRequest request) {
    return ApiResponse.ok(service.getMyAddresses(), request);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<AddressResponse> create(
      @Valid @RequestBody AddressRequest req,
      HttpServletRequest request) {
    return ApiResponse.ok(service.createAddress(req), request);
  }

  @PutMapping("/{id}")
  public ApiResponse<AddressResponse> update(
      @PathVariable Long id,
      @Valid @RequestBody AddressRequest req,
      HttpServletRequest request) {
    return ApiResponse.ok(service.updateAddress(id, req), request);
  }

  @DeleteMapping("/{id}")
  public ApiResponse<String> delete(@PathVariable Long id, HttpServletRequest request) {
    service.deleteAddress(id);
    return ApiResponse.ok("Eliminado exitosamente", request);
  }
}
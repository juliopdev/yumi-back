package com.yumi.address.application.mapper;

import java.time.Instant;

import com.yumi.address.application.dto.AddressRequest;
import com.yumi.address.application.dto.AddressResponse;
import com.yumi.address.domain.Address;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Transformaciones entre modelo y DTO.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AddressMapper {

  public static Address toEntity(AddressRequest req, String ownerEmail) {
    return Address.builder()
        .ownerEmail(ownerEmail)
        .city(req.city().trim())
        .state(req.state().trim())
        .zipCode(req.zipCode().trim())
        .country(req.country().trim())
        .street(req.street() != null ? req.street().trim() : null)
        .build();
  }

  public static AddressResponse toResponse(Address a) {
    return new AddressResponse(
        a.getId(),
        a.getCity(),
        a.getState(),
        a.getZipCode(),
        a.getCountry(),
        a.getUpdatedAt() != null ? a.getUpdatedAt() : Instant.now());
  }
}
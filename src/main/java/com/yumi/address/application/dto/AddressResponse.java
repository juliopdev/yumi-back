package com.yumi.address.application.dto;

import java.time.Instant;

public record AddressResponse(
    Long id,
    String city,
    String state,
    String zipCode,
    String country,
    Instant updatedAt) {
}
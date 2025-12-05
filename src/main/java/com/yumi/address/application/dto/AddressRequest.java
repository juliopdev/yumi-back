package com.yumi.address.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
    Long id,
    @NotBlank String city,
    @NotBlank String state,
    @NotBlank String zipCode,
    @NotBlank String country,
    String street) {
}
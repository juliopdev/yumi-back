package com.yumi.address.application.dto;

public record EditAddressRequest(
    Long id,
    String city,
    String state,
    String zipCode,
    String country) {
}
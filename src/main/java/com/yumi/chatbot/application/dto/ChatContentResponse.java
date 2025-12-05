package com.yumi.chatbot.application.dto;

import java.util.List;

import com.yumi.catalog.application.dto.ProductResponse;

public record ChatContentResponse(
    String reply,
    List<ProductResponse> products,
    boolean addToCart,
    boolean isUserAuthenticated) {
}

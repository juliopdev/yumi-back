package com.yumi.about.application.dto;

import java.util.List;

public record AboutResponse(
    String key,
    String title,
    String subtitle,
    List<CardResponse> cards) {
}
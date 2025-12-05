package com.yumi.chatbot.core;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class DeliveryEstimator {

  @Cacheable("delivery-time")
  public String estimate(String city, String zipCode) {
    if (city == null) return "48-72 h";
    String c = city.toLowerCase();
    if (c.contains("lima") || c.contains("bogotá") || c.contains("cdmx"))
      return "24-48 h";
    if (zipCode != null && zipCode.startsWith("1"))
      return "24-48 h";
    return "48-72 h";
  }
}
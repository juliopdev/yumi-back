package com.yumi.order.application;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Genera SKUs únicas para pedidos.
 */
@Component
public class OrderNumberGenerator {

  private static final String PREFIX = "ORD";
  private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyMMddHHmmss");

  public String next() {
    return PREFIX + LocalDateTime.now().format(FORMAT)
        + (int) (Math.random() * 1_000); // 0-999
  }
}
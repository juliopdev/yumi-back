package com.yumi.catalog.application;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.yumi.catalog.domain.Product;

public class ProductSpecifications {
  public static Specification<Product> visibleTrue() {
    return (root, query, cb) -> cb.isTrue(root.get("visible"));
  }

  public static Specification<Product> categoryName(String name) {
    return (root, query, cb) -> cb.equal(cb.lower(root.get("category").get("slug")), name.toLowerCase());
  }

  public static Specification<Product> nameOrDescriptionContaining(String text) {
    return (root, query, cb) -> cb.or(
        cb.like(cb.lower(root.get("name")), "%" + text.toLowerCase() + "%"),
        cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%"));
  }

  public static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
    return (root, query, cb) -> cb.between(root.get("price"), min, max);
  }
}
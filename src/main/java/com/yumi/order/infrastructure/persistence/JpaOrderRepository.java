package com.yumi.order.infrastructure.persistence;

import com.yumi.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaOrderRepository extends JpaRepository<Order, Long> {

  Optional<Order> findBySku(String sku);

  Optional<Order> findBySkuAndCustomerEmail(String sku, String email);

  @Query("select o from Order o where o.customerEmail = :email order by o.createdAt desc")
  Page<Order> findByCustomerEmailOrderByCreatedAtDesc(@Param("email") String email, Pageable pageable);

  @Query("SELECT o FROM Order o JOIN FETCH o.items JOIN FETCH o.address ORDER BY o.createdAt DESC")
  Page<Order> findAllWithItemsAndAddress(Pageable pageable);
}
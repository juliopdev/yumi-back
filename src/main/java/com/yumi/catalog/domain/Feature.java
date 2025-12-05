package com.yumi.catalog.domain;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "features", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "value" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feature {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false)
  private String value;

  @ManyToMany(mappedBy = "features")
  @Builder.Default
  private Set<Category> categories = new HashSet<>();

  @ManyToMany(mappedBy = "features")
  @Builder.Default
  private Set<Product> products = new HashSet<>();

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private Instant createdAt;
}
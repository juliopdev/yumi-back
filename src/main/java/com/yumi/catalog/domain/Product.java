package com.yumi.catalog.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, length = 8)
  private String sku;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  private String imageUrl;

  private BigDecimal price;

  private Integer stock;

  @Builder.Default
  private Boolean visible = true;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @ManyToMany
  @JoinTable(name = "product_feature", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "feature_id"))
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  @Builder.Default
  private Set<Feature> features = new HashSet<>();

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private Instant createdAt;

  @PrePersist
  void generateSku() {
    this.sku = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }

  /* helpers */
  public void addFeature(Feature f) {
    features.add(f);
    f.getProducts().add(this);
  }

  public void removeFeature(Feature f) {
    features.remove(f);
    f.getProducts().remove(this);
  }

  public void clearFeatures() {
    for (Feature f : new HashSet<>(features)) {
      removeFeature(f);
    }
  }
}
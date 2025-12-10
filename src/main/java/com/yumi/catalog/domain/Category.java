package com.yumi.catalog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Cache;

@Entity
@Table(name = "categories")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String slug;

  @Column(nullable = false)
  private String name;

  private String description;

  @OneToMany(mappedBy = "category")
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  @Builder.Default
  private List<Product> products = new ArrayList<>();

  @ManyToMany
  @JoinTable(name = "category_feature", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns = @JoinColumn(name = "feature_id"))
  @Builder.Default
  private Set<Feature> features = new HashSet<>();

  @Builder.Default
  private Boolean visible = true;

  private Long totalProducts;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private Instant createdAt;

  @PrePersist
  @PreUpdate
  private void slugify() {
    this.slug = name == null ? null
        : name.toLowerCase()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", "");
  }

  /* helpers */
  public void addFeature(Feature f) {
    features.add(f);
    f.getCategories().add(this);
  }

  public void changeData(String name, String slug, String description) {
    this.name = name;
    this.slug = slug;
    this.description = description;
  }
}
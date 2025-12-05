package com.yumi.catalog.infrastructure.persistence;

import com.yumi.catalog.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface JpaProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

  /* ---------- Exists ---------- */
  boolean existsBySku(String sku);

  boolean existsByName(String name);

  /* ---------- Búsqueda por SKU exacto ---------- */
  Optional<Product> findBySku(String sku);

  /* ---------- Búsqueda "like" ---------- */
  List<Product> findByNameContainingIgnoreCaseAndVisibleTrue(String namePart);

  /* ---------- Búsqueda por visibilidad ---------- */
  Optional<Product> findByVisibleTrueAndSku(String sku);

  @Query("SELECT p FROM Product p WHERE p.visible = true AND p.sku IN :skus")
  List<Product> findVisibleBySkus(@Param("skus") Set<String> sku);

  Page<Product> findAllByVisibleTrue(Pageable pageable);

  Page<Product> findAllByVisibleFalse(Pageable pageable);

  /* ---------- Búsqueda por categoría ---------- */
  Page<Product> findAllByCategoryIdAndVisibleTrue(Long categoryId, Pageable pageable);

  Page<Product> findAllByCategoryNameIgnoreCaseAndVisibleTrue(String categoryName, Pageable pageable);

  /* ---------- Búsqueda por característica ---------- */
  @Query("SELECT DISTINCT p FROM Product p JOIN p.features f WHERE f.id = :featureId AND p.visible = true")
  Page<Product> findAllByFeatureIdAndVisibleTrue(@Param("featureId") Long featureId, Pageable pageable);

  @Query("SELECT DISTINCT p FROM Product p JOIN p.features f WHERE f.name = :featureName AND p.visible = true")
  Page<Product> findAllByFeatureNameAndVisibleTrue(@Param("featureName") String featureName, Pageable pageable);

  /* ---------- Búsqueda rápida por texto en nombre o descripción ---------- */
  @Query("SELECT p FROM Product p " +
      "WHERE p.visible = true AND " +
      "(LOWER(p.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
      "LOWER(p.description) LIKE LOWER(CONCAT('%', :text, '%')))")
  Page<Product> quickSearchVisibleProducts(@Param("text") String text, Pageable pageable);

  /* ---------- Productos aleatorios visibles ---------- */
  @Query(value = "SELECT * FROM products WHERE visible = true ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
  List<Product> findRandomVisibleProducts(@Param("limit") int limit);

  /* ---------- Fetch con categoría y características ---------- */
  @Query("SELECT p FROM Product p " +
      "JOIN FETCH p.category " +
      "LEFT JOIN FETCH p.features " +
      "WHERE p.id = :id")
  Optional<Product> findByIdWithCategoryAndFeatures(@Param("id") Long id);

  /* ---------- Fetch con solo características ---------- */
  @Query("SELECT p FROM Product p " +
      "LEFT JOIN FETCH p.features " +
      "WHERE p.id = :id")
  Optional<Product> findByIdWithFeatures(@Param("id") Long id);

  /* ---------- Búsqueda por múltiples SKUs (bulk) ---------- */
  List<Product> findAllBySkuIn(Set<String> skus);

  /* ---------- Búsqueda por múltiples IDs (bulk) ---------- */
  List<Product> findAllByIdIn(Set<Long> ids);

  /* ---------- Búsqueda por rango de precio ---------- */
  @Query("SELECT p FROM Product p " +
      "WHERE p.visible = true AND p.price BETWEEN :minPrice AND :maxPrice")
  Page<Product> findAllByPriceRange(@Param("minPrice") BigDecimal minPrice,
      @Param("maxPrice") BigDecimal maxPrice,
      Pageable pageable);

  /* ---------- Contar productos visibles por categoría ---------- */
  @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId AND p.visible = true")
  long countVisibleByCategoryId(@Param("categoryId") Long categoryId);

  /* ---------- Contar productos visibles por característica ---------- */
  @Query("SELECT COUNT(DISTINCT p) FROM Product p JOIN p.features f WHERE f.id = :featureId AND p.visible = true")
  long countAllByFeatureIdAndVisibleTrue(@Param("featureId") Long featureId);

  /* ---------- Soft delete / restore ---------- */
  @Modifying
  @Query("UPDATE Product p SET p.visible = false WHERE p.id = :id")
  void softDeleteById(@Param("id") Long id);

  @Modifying
  @Query("UPDATE Product p SET p.visible = true WHERE p.id = :id")
  void softRestoreById(@Param("id") Long id);

  /* para batch */
  @Modifying
  @Query("UPDATE Product p SET p.visible = false WHERE p.id IN :ids")
  void softDeleteAllById(@Param("ids") Collection<Long> ids);
}
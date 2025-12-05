package com.yumi.catalog.infrastructure.persistence;

import com.yumi.catalog.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaCategoryRepository extends JpaRepository<Category, Long> {

  /* ---------- exists ---------- */
  boolean existsByName(String name);

  boolean existsBySlug(String slug);

  /* ---------- búsqueda simple ---------- */
  Optional<Category> findBySlug(String slug);

  /* ---------- búsqueda "like" ---------- */
  List<Category> findByNameContainingIgnoreCase(String namePart);

  Page<Category> findByNameContainingIgnoreCase(String namePart, Pageable pageable);

  /* ---------- fetch con productos (evita N+1) ---------- */
  @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
  Optional<Category> findWithProductsById(@Param("id") Long id);

  /* ---------- fetch con productos y sus características ---------- */
  @Query("SELECT DISTINCT c FROM Category c "
      + "LEFT JOIN FETCH c.products p "
      + "LEFT JOIN FETCH p.features "
      + "WHERE c.id = :id")
  Optional<Category> findWithProductsAndFeaturesById(@Param("id") Long id);

  /* ---------- fetch con solo las características de la categoría ---------- */
  @Query("SELECT c FROM Category c LEFT JOIN FETCH c.features WHERE c.id = :id")
  Optional<Category> findWithFeaturesById(@Param("id") Long id);

  /* ---------- listado de slugs  ---------- */
  @Query("SELECT c.slug FROM Category c")
  List<String> findAllSlugs();

  /* ---------- Contar productos visibles por característica ---------- */
  @Query("SELECT COUNT(DISTINCT p) FROM Product p JOIN p.features f WHERE f.id = :featureId")
  long countAllByFeatureId(@Param("featureId") Long featureId);
}
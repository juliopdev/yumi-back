package com.yumi.catalog.infrastructure.persistence;

import com.yumi.catalog.domain.Feature;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JpaFeatureRepository extends JpaRepository<Feature, Long> {

  /* ---------- exists ---------- */
  boolean existsByNameAndValue(String name, String value);

  boolean existsByName(String name);

  /* ---------- búsqueda exacta ---------- */
  Optional<Feature> findByNameIgnoreCaseAndValueIgnoreCase(String name, String value);

  /* ---------- búsqueda "like" ---------- */
  List<Feature> findByNameContainingIgnoreCase(String namePart);

  List<Feature> findByValueContainingIgnoreCase(String valuePart);

  Page<Feature> findByNameContainingIgnoreCase(String namePart, Pageable pageable);

  /* ---------- fetch con categorías ---------- */
  @Query("SELECT f FROM Feature f LEFT JOIN FETCH f.categories WHERE f.id = :id")
  Optional<Feature> findWithCategoriesById(@Param("id") Long id);

  /* ---------- fetch con productos ---------- */
  @Query("SELECT f FROM Feature f LEFT JOIN FETCH f.products WHERE f.id = :id")
  Optional<Feature> findWithProductsById(@Param("id") Long id);

  /* ---------- fetch con categorías y productos ---------- */
  @Query("SELECT DISTINCT f FROM Feature f "
      + "LEFT JOIN FETCH f.categories "
      + "LEFT JOIN FETCH f.products "
      + "WHERE f.id = :id")
  Optional<Feature> findWithCategoriesAndProductsById(@Param("id") Long id);

  /* ---------- bulk: solo IDs ---------- */
  List<Feature> findAllByIdIn(Collection<Long> ids);
}
package com.yumi.catalog.application;

import com.yumi.catalog.application.dto.*;
import com.yumi.catalog.application.mapper.CategoryMapper;
import com.yumi.catalog.application.mapper.ProductMapper;
import com.yumi.catalog.domain.Category;
import com.yumi.catalog.infrastructure.persistence.JpaCategoryRepository;
import com.yumi.catalog.infrastructure.persistence.JpaProductRepository;
import com.yumi.shared.exception.DuplicateResourceException;
import com.yumi.shared.exception.ResourceNotFoundException;
import com.yumi.shared.util.StringNormalizer;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

  private final JpaCategoryRepository categoryRepository;
  private final JpaProductRepository productRepository;

  /* ---------- Listado PAGINADO ---------- */
  public Page<CategoryResponse> findAll(Pageable pageable) {
    return categoryRepository.findAll(pageable)
        .map(CategoryMapper::toResponse);
  }

  public CategoryWithProductsResponse findBySlugWithProducts(String slug, int productLimit) {
    Category cat = categoryRepository.findBySlug(slug)
        .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    List<ProductResponse> products = productRepository
        .findAllByCategoryIdAndVisibleTrue(cat.getId(), Pageable.ofSize(productLimit))
        .map(ProductMapper::toResponse)
        .toList();
    long total = productRepository.countVisibleByCategoryId(cat.getId());
    return CategoryMapper.toResponseWithProducts(cat, products, total);
  }

  public Page<CategoryResponse> findByNameContaining(String namePart, Pageable pageable) {
    return categoryRepository.findByNameContainingIgnoreCase(namePart, pageable)
        .map(CategoryMapper::toResponse);
  }

  /* ---------- Detalle con productos ---------- */
  public CategoryResponse findBySlug(String slug) {
    Category cat = categoryRepository.findBySlug(slug)
        .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    return CategoryMapper.toResponse(cat);
  }

  /* ---------- Detalle con productos + features ---------- */
  public CategoryWithProductsResponse findBySlugWithProductsAndFeatures(String slug, int productLimit) {
    Category cat = categoryRepository.findWithProductsAndFeaturesById(
        categoryRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"))
            .getId())
        .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    List<ProductResponse> products = cat.getProducts()
        .stream()
        .limit(productLimit)
        .map(ProductMapper::toResponse)
        .toList();
    long total = productRepository.countVisibleByCategoryId(cat.getId());

    return CategoryMapper.toResponseWithProducts(cat, products, total);
  }

  /* ---------- Alta ---------- */
  @Transactional
  public CategoryResponse create(CategoryRequest req) {
    String slug = StringNormalizer.toSlug(req.name());
    if (categoryRepository.existsBySlug(slug))
      throw new DuplicateResourceException("Slug duplicado");

    Category cat = Category.builder()
        .name(req.name().trim())
        .slug(slug)
        .description(req.description() != null ? req.description().trim() : "")
        .build();

    return CategoryMapper.toResponse(categoryRepository.save(cat));
  }

  /* ---------- Modificación ---------- */
  @Transactional
  public CategoryResponse update(String oldSlug, CategoryRequest req) {
    Category cat = categoryRepository.findBySlug(oldSlug)
        .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

    String newSlug = StringNormalizer.toSlug(req.name());
    if (!oldSlug.equals(newSlug) && categoryRepository.existsBySlug(newSlug))
      throw new DuplicateResourceException("Nombre duplicado");

    cat.changeData(req.name().trim(), newSlug,
        req.description() != null ? req.description().trim() : null);

    return CategoryMapper.toResponse(cat);
  }

  /* ---------- Soft delete ---------- */
  @Transactional
  public void delete(String slug) {
    Category cat = categoryRepository.findBySlug(slug)
        .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

    long visibleProducts = productRepository.countVisibleByCategoryId(cat.getId());
    if (visibleProducts > 0)
      throw new IllegalStateException("No se puede eliminar: la categoría tiene productos activos");

    categoryRepository.delete(cat);
  }
}
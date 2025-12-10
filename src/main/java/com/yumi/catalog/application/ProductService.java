package com.yumi.catalog.application;

import com.yumi.catalog.application.dto.*;
import com.yumi.catalog.application.mapper.ProductMapper;
import com.yumi.catalog.domain.*;
import com.yumi.catalog.infrastructure.persistence.*;
import com.yumi.shared.exception.BadRequestException;
import com.yumi.shared.exception.DuplicateResourceException;
import com.yumi.shared.exception.ResourceNotFoundException;
import com.yumi.shared.util.FileValidator;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

  private final JpaProductRepository productRepository;
  private final JpaCategoryRepository categoryRepository;
  private final JpaFeatureRepository featureRepository;
  private final Cloudinary cloudinary;

  public ProductResponse getById(Long id) {
    return productRepository.findByIdWithCategoryAndFeatures(id)
        .map(ProductMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
  }

  /* ---------- Listados paginados ---------- */
  public Page<ProductResponse> getAll(Pageable pageable) {
    return productRepository.findAllByVisibleTrue(pageable)
        .map(ProductMapper::toResponse);
  }

  public List<ProductResponse> getAllBySkus(Set<String> skus) {
    return productRepository.findVisibleBySkus(skus).stream()
        .map(ProductMapper::toResponse).toList();
  }

  public Page<ProductResponse> getAllVisibleByCategoryId(Long categoryId, Pageable pageable) {
    return productRepository.findAllByCategoryIdAndVisibleTrue(categoryId, pageable)
        .map(ProductMapper::toResponse);
  }

  public Page<ProductResponse> getAllVisibleByFeatureName(String featureName, Pageable pageable) {
    return productRepository.findAllByFeatureNameAndVisibleTrue(featureName, pageable)
        .map(ProductMapper::toResponse);
  }

  public Page<ProductResponse> getAllVisibleByCategoryName(String categoryName, Pageable pageable) {
    return productRepository.findAllByCategoryNameIgnoreCaseAndVisibleTrue(categoryName, pageable)
        .map(ProductMapper::toResponse);
  }

  public Page<ProductResponse> getAllVisibleByFeatureId(Long featureId, Pageable pageable) {
    return productRepository.findAllByFeatureIdAndVisibleTrue(featureId, pageable)
        .map(ProductMapper::toResponse);
  }

  public Page<ProductResponse> getAllVisibleByPriceRange(BigDecimal min, BigDecimal max, Pageable pageable) {
    return productRepository.findAllByPriceRange(min, max, pageable)
        .map(ProductMapper::toResponse);
  }

  public Page<ProductResponse> searchVisible(String text, Pageable pageable) {
    return productRepository.quickSearchVisibleProducts(text, pageable)
        .map(ProductMapper::toResponse);
  }

  public Page<ProductResponse> getAllVisibleWithFilters(
      String category,
      String search,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      Pageable pageable) {

    Specification<Product> spec = Specification.where(ProductSpecifications.visibleTrue());

    if (category != null && !category.isBlank()) {
      spec = spec.and(ProductSpecifications.categoryName(category));
    }
    if (search != null && !search.isBlank()) {
      spec = spec.and(ProductSpecifications.nameOrDescriptionContaining(search));
    }
    if (minPrice != null && maxPrice != null) {
      spec = spec.and(ProductSpecifications.priceBetween(minPrice, maxPrice));
    }

    return productRepository.findAll(spec, pageable).map(ProductMapper::toResponse);
  }

  /* ---------- Crear ---------- */
  @Transactional
  public ProductResponse create(CreateProductRequest req) {
    if (productRepository.existsBySku(req.sku()))
      throw new DuplicateResourceException("SKU duplicado");

    Category cat = categoryRepository.findById(req.categoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

    Product p = Product.builder()
        .sku(req.sku().trim())
        .name(req.name().trim())
        .description(req.description() != null ? req.description().trim() : "")
        .price(req.price())
        .stock(req.stock())
        .category(cat)
        .visible(true)
        .build();

    if (req.features() != null && !req.features().isEmpty()) {
      List<Feature> features = getOrCreateFeatures(req.features());
      features.forEach(p::addFeature);
    }

    if (!FileValidator.isImage(req.image()))
      throw new BadRequestException("Imagen no válida (jpg, jpeg, png, webp ≤ 5 MB)");

    String url = uploadToCloudinary(req.image());
    p.setImageUrl(url);

    return ProductMapper.toResponse(productRepository.save(p));
  }

  /* ---------- Actualizar ---------- */
  @Transactional
  public ProductResponse update(Long id, EditProductRequest req) {
    Product p = productRepository.findByIdWithCategoryAndFeatures(id)
        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

    if (req.sku() != null && !p.getSku().equals(req.sku()) && productRepository.existsBySku(req.sku()))
      throw new DuplicateResourceException("SKU duplicado");

    if (req.name() != null && !p.getName().equals(req.name().trim()) && productRepository.existsByName(req.name()))
      throw new DuplicateResourceException("Nombre duplicado");

    Optional.ofNullable(req.sku()).ifPresent(v -> p.setSku(v.trim()));
    Optional.ofNullable(req.name()).ifPresent(v -> p.setName(v.trim()));
    Optional.ofNullable(req.description()).ifPresent(v -> p.setDescription(v.trim()));
    Optional.ofNullable(req.price()).ifPresent(p::setPrice);
    Optional.ofNullable(req.stock()).ifPresent(p::setStock);

    if (req.categoryId() != null) {
      Category cat = categoryRepository.findById(req.categoryId())
          .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
      p.setCategory(cat);
    }

    if (req.features() != null) {
      p.clearFeatures();
      List<Feature> features = getOrCreateFeatures(req.features());
      features.forEach(p::addFeature);
    }

    if (req.image() != null) {
      if (!FileValidator.isImage(req.image()))
        throw new BadRequestException("Imagen no válida (jpg, jpeg, png, webp ≤ 5 MB)");
      deleteCloudinaryImage(p.getImageUrl());
      String url = uploadToCloudinary(req.image());
      p.setImageUrl(url);
    }

    return ProductMapper.toResponse(productRepository.save(p));
  }

  /* ---------- Soft delete ---------- */
  @Transactional
  public void delete(Long id) {
    if (!productRepository.existsById(id))
      throw new ResourceNotFoundException("Producto no encontrado");
    productRepository.softDeleteById(id);
  }

  /* ---------- Restaurar ---------- */
  @Transactional
  public void restore(Long id) {
    if (!productRepository.existsById(id))
      throw new ResourceNotFoundException("Producto no encontrado");
    productRepository.softRestoreById(id);
  }

  /* ---------- Otros ---------- */
  @Cacheable("recommendations")
  public List<ProductResponse> randomVisible(int limit) {
    return productRepository.findRandomVisibleProducts(limit)
        .stream().map(ProductMapper::toResponse).toList();
  }

  public List<ProductResponse> findByNameContaining(String namePart) {
    return productRepository.findByNameContainingIgnoreCaseAndVisibleTrue(namePart)
        .stream().map(ProductMapper::toResponse).toList();
  }

  /* ---------- Helpers privados ---------- */
  private List<Feature> getOrCreateFeatures(List<FeatureRequest> requests) {
    Set<String> seen = new HashSet<>();
    List<FeatureRequest> uniqueRequests = requests.stream()
        .filter(r -> r.name() != null && r.value() != null)
        .filter(r -> seen.add(r.name().trim().toLowerCase() + "|" + r.value().trim().toLowerCase()))
        .toList();

    List<Feature> existing = new ArrayList<>();
    for (FeatureRequest req : uniqueRequests) {
      featureRepository.findByNameIgnoreCaseAndValueIgnoreCase(req.name().trim(), req.value().trim())
          .ifPresent(existing::add);
    }

    List<Feature> toSave = uniqueRequests.stream()
        .filter(r -> existing.stream()
            .noneMatch(f -> f.getName().equalsIgnoreCase(r.name().trim()) &&
                f.getValue().equalsIgnoreCase(r.value().trim())))
        .map(r -> Feature.builder()
            .name(r.name().trim())
            .value(r.value().trim())
            .build())
        .toList();

    if (!toSave.isEmpty()) {
      featureRepository.saveAll(toSave);
    }

    existing.addAll(toSave);
    return existing;
  }

  private String uploadToCloudinary(MultipartFile file) {
    try {
      Map<?, ?> result = cloudinary.uploader()
          .upload(file.getBytes(), ObjectUtils.asMap("folder", "yumi/products"));
      return (String) result.get("secure_url");
    } catch (Exception e) {
      throw new RuntimeException("Error al subir imagen", e);
    }
  }

  private void deleteCloudinaryImage(String url) {
    if (url == null)
      return;
    try {
      String publicId = extractPublicId(url);
      cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    } catch (Exception e) {
      throw new RuntimeException("No se pudo eliminar la imagen de Cloudinary", e);
    }
  }

  private String extractPublicId(String url) {
    int uploadIndex = url.lastIndexOf("/upload/");
    if (uploadIndex == -1)
      return url;
    String afterUpload = url.substring(uploadIndex + 8);
    int slashIndex = afterUpload.indexOf('/');
    if (slashIndex == -1)
      return url;
    String withVersion = afterUpload.substring(slashIndex + 1);
    int lastDot = withVersion.lastIndexOf('.');
    return lastDot == -1 ? withVersion : withVersion.substring(0, lastDot);
  }
}
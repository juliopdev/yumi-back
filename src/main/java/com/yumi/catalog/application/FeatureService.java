package com.yumi.catalog.application;

import com.yumi.catalog.application.dto.*;
import com.yumi.catalog.application.mapper.FeatureMapper;
import com.yumi.catalog.application.mapper.ProductMapper;
import com.yumi.catalog.domain.Feature;
import com.yumi.catalog.infrastructure.persistence.JpaCategoryRepository;
import com.yumi.catalog.infrastructure.persistence.JpaFeatureRepository;
import com.yumi.catalog.infrastructure.persistence.JpaProductRepository;
import com.yumi.shared.exception.DuplicateResourceException;
import com.yumi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeatureService {

  private final JpaFeatureRepository featureRepository;
  private final JpaProductRepository productRepository;
  private final JpaCategoryRepository categoryRepository;

  /* ---------- Listado / búsqueda básica ---------- */
  public Page<FeatureResponse> findAll(Pageable pageable) {
    return featureRepository.findAll(pageable)
        .map(FeatureMapper::toResponse);
  }

  public FeatureResponse findById(Long id) {
    return featureRepository.findById(id)
        .map(FeatureMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Característica no encontrada"));
  }

  public Page<FeatureResponse> findByNameContaining(String namePart, Pageable pageable) {
    return featureRepository.findByNameContainingIgnoreCase(namePart, pageable)
        .map(FeatureMapper::toResponse);
  }

  /* ---------- Detalle con productos visibles ---------- */
  public FeatureSearchResponse findFeatureWithProducts(Long featureId, int limit) {
    Feature f = featureRepository.findWithProductsById(featureId)
        .orElseThrow(() -> new ResourceNotFoundException("Característica no encontrada"));

    List<ProductResponse> prods = productRepository.findAllByFeatureIdAndVisibleTrue(featureId, Pageable.ofSize(limit))
        .map(ProductMapper::toResponse)
        .toList();

    return new FeatureSearchResponse(FeatureMapper.toResponse(f), prods);
  }

  /* ---------- Detalle con categorías ---------- */
  public FeatureResponse findFeatureWithCategories(Long featureId) {
    Feature f = featureRepository.findWithCategoriesById(featureId)
        .orElseThrow(() -> new ResourceNotFoundException("Característica no encontrada"));
    return FeatureMapper.toResponse(f);
  }

  /* ---------- Detalle con categorías y productos ---------- */
  public FeatureResponse findFeatureWithCategoriesAndProducts(Long featureId) {
    Feature f = featureRepository.findWithCategoriesAndProductsById(featureId)
        .orElseThrow(() -> new ResourceNotFoundException("Característica no encontrada"));
    return FeatureMapper.toResponse(f);
  }

  /* ---------- Alta ---------- */
  @Transactional
  public FeatureResponse create(FeatureRequest req) {
    if (featureRepository.existsByNameAndValue(req.name(), req.value()))
      throw new DuplicateResourceException("Característica duplicada");

    Feature f = Feature.builder()
        .name(req.name().trim())
        .value(req.value().trim())
        .build();
    f = featureRepository.save(f);
    return FeatureMapper.toResponse(f);
  }

  /* ---------- Actualización ---------- */
  @Transactional
  public FeatureResponse update(Long id, FeatureRequest req) {
    Feature f = featureRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Característica no encontrada"));

    if ((!f.getName().equalsIgnoreCase(req.name()) || !f.getValue().equalsIgnoreCase(req.value()))
        && featureRepository.existsByNameAndValue(req.name(), req.value()))
      throw new DuplicateResourceException("Característica duplicada");

    f.setName(req.name().trim());
    f.setValue(req.value().trim());
    return FeatureMapper.toResponse(f);
  }

  /* ---------- Soft delete ---------- */
  @Transactional
  public void delete(Long featureId) {
    long usedInProducts = productRepository.countAllByFeatureIdAndVisibleTrue(featureId);
    long usedInCategories = categoryRepository.countAllByFeatureId(featureId);

    if (usedInProducts > 0 || usedInCategories > 0) {
      throw new IllegalStateException(
          "No se puede eliminar: hay categorías o productos activos que usan esta característica");
    }
    featureRepository.deleteById(featureId);
  }

  /* ---------- Bulk: obtener o crear features ---------- */
  @Transactional
  public List<FeatureResponse> getOrCreateAll(List<FeatureRequest> requests) {
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
    return existing.stream().map(FeatureMapper::toResponse).toList();
  }
}
package com.yumi.cart.application;

import com.yumi.cart.application.dto.ProductSnapshot;
import com.yumi.catalog.application.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Puente de lectura hacia catálogo sin dependencia circular.
 */
@Component
@RequiredArgsConstructor
public class ProductCatalogReader {

    private final ProductService productService;

    public List<ProductSnapshot> snapshotsBySkus(Set<String> skus) {
        return productService.getAllBySkus(skus).stream()
                .map(p -> new ProductSnapshot(p.sku(), p.name(), p.imageUrl(), p.price()))
                .toList();
    }
}
package com.threepm.api.toko.Service.impl;

import com.threepm.api.toko.Model.Entity.Products;
import com.threepm.api.toko.Model.Entity.Stocks;
import com.threepm.api.toko.Repository.ProductsRepository;
import com.threepm.api.toko.Repository.StocksRepository;
import com.threepm.api.toko.Model.Request.ProductRequest;
import com.threepm.api.toko.Model.Response.ProductResponse;
import com.threepm.api.toko.Service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductsRepository productsRepository;
    private final StocksRepository stocksRepository;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        log.info("[PRODUCT_CREATE] sku={}", request.getSku());

        if (productsRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("SKU already exists");
        }

        Products product = Products.builder()
                .sku(request.getSku())
                .productName(request.getProductName())
                .purchasePrice(request.getPurchasePrice())
                .sellingPrice(request.getSellingPrice())
                .description(request.getDescription())
                .build();

        product = productsRepository.save(product);

        Stocks stock = Stocks.builder()
                .product(product)
                .quantity(0)
                .build();

        stocksRepository.save(stock);

        return toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        log.info("[PRODUCT_UPDATE] id={}", id);

        Products product = productsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productsRepository.findBySku(request.getSku())
                .ifPresent(existingProduct -> {
                    if (!existingProduct.getId().equals(id)) {
                        throw new RuntimeException("SKU already exists");
                    }
                });

        product.setSku(request.getSku());
        product.setProductName(request.getProductName());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setDescription(request.getDescription());

        product = productsRepository.save(product);

        return toResponse(product);
    }

    @Override
    public ProductResponse findById(Long id) {
        Products product = productsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return toResponse(product);
    }

    @Override
    public List<ProductResponse> findAll() {
        return productsRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("[PRODUCT_DELETE] id={}", id);

        Products product = productsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productsRepository.delete(product);
    }

    private ProductResponse toResponse(Products product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .productName(product.getProductName())
                .purchasePrice(product.getPurchasePrice())
                .sellingPrice(product.getSellingPrice())
                .description(product.getDescription())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
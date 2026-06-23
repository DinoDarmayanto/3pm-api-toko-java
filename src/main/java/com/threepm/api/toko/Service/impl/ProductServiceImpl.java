package com.threepm.api.toko.Service.impl;

import com.threepm.api.toko.Exception.ResourceNotFoundException;
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
        log.info("[PRODUCT_CREATE] Start | sku={} | productName={}", request.getSku(), request.getProductName());

        if (productsRepository.existsBySku(request.getSku())) {
            log.warn("[PRODUCT_CREATE] Failed | duplicate sku={}", request.getSku());
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

        log.info("[PRODUCT_CREATE] Product saved | id={} | sku={}", product.getId(), product.getSku());

        Stocks stock = Stocks.builder()
                .product(product)
                .quantity(0)
                .build();

        stocksRepository.save(stock);

        log.info("[PRODUCT_CREATE] Initial stock created | productId={} | quantity=0", product.getId());
        log.info("[PRODUCT_CREATE] Success | id={} | sku={} | productName={}",
                product.getId(), product.getSku(), product.getProductName());

        return toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        log.info("[PRODUCT_UPDATE] Start | id={} | sku={} | productName={}",
                id, request.getSku(), request.getProductName());

        Products product = productsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[PRODUCT_UPDATE] Failed | product not found | id={}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });

        log.info("[PRODUCT_UPDATE] Existing product found | id={} | oldSku={} | oldName={}",
                product.getId(), product.getSku(), product.getProductName());

        productsRepository.findBySku(request.getSku())
                .ifPresent(existingProduct -> {
                    if (!existingProduct.getId().equals(id)) {
                        log.warn("[PRODUCT_UPDATE] Failed | duplicate sku={} | existingProductId={}",
                                request.getSku(), existingProduct.getId());
                        throw new RuntimeException("SKU already exists");
                    }
                });

        product.setSku(request.getSku());
        product.setProductName(request.getProductName());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setDescription(request.getDescription());

        product = productsRepository.save(product);

        log.info("[PRODUCT_UPDATE] Success | id={} | sku={} | productName={}",
                product.getId(), product.getSku(), product.getProductName());

        return toResponse(product);
    }

    @Override
    public ProductResponse findById(Long id) {
        log.info("[PRODUCT_FIND_BY_ID] Start | id={}", id);

        Products product = productsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[PRODUCT_FIND_BY_ID] Failed | product not found | id={}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });

        log.info("[PRODUCT_FIND_BY_ID] Success | id={} | sku={} | productName={}",
                product.getId(), product.getSku(), product.getProductName());

        return toResponse(product);
    }

    @Override
    public List<ProductResponse> findAll() {
        log.info("[PRODUCT_FIND_ALL] Start");

        List<Products> products = productsRepository.findAll();

        log.info("[PRODUCT_FIND_ALL] Success | totalData={}", products.size());

        return products.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("[PRODUCT_DELETE] Start | id={}", id);

        Products product = productsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[PRODUCT_DELETE] Failed | product not found | id={}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });

        productsRepository.delete(product);

        log.info("[PRODUCT_DELETE] Success | id={} | sku={} | productName={}",
                product.getId(), product.getSku(), product.getProductName());
    }

    private ProductResponse toResponse(Products product) {
        log.debug("[PRODUCT_MAPPING] Mapping entity to response | id={} | sku={}",
                product.getId(), product.getSku());

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
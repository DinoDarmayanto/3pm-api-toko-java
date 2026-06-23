package com.threepm.api.toko.Service.impl;

import com.threepm.api.toko.Exception.ResourceNotFoundException;
import com.threepm.api.toko.Model.Entity.Stocks;
import com.threepm.api.toko.Repository.StocksRepository;
import com.threepm.api.toko.Model.Response.StockResponse;
import com.threepm.api.toko.Service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {

    private final StocksRepository stocksRepository;

    @Override
    public StockResponse getStock(Long productId) {
        log.info("[STOCK_GET] Start | productId={}", productId);

        Stocks stock = stocksRepository.findByProduct_Id(productId)
                .orElseThrow(() -> {
                    log.warn("[STOCK_GET] Failed | stock not found | productId={}", productId);
                    return new ResourceNotFoundException("Stock not found for productId: " + productId);
                });

        log.info("[STOCK_GET] Stock found | stockId={} | productId={} | sku={} | productName={} | quantity={}",
                stock.getId(),
                stock.getProduct().getId(),
                stock.getProduct().getSku(),
                stock.getProduct().getProductName(),
                stock.getQuantity());

        StockResponse response = toResponse(stock);

        log.info("[STOCK_GET] Success | productId={} | quantity={}",
                response.getProductId(),
                response.getQuantity());

        return response;
    }

    @Override
    public List<StockResponse> getAllStocks() {
        log.info("[STOCK_GET_ALL] Start");

        List<Stocks> stocks = stocksRepository.findAll();

        log.info("[STOCK_GET_ALL] Data loaded | totalStockData={}", stocks.size());

        List<StockResponse> responses = stocks.stream()
                .map(this::toResponse)
                .toList();

        log.info("[STOCK_GET_ALL] Success | totalResponseData={}", responses.size());

        return responses;
    }

    private StockResponse toResponse(Stocks stock) {
        log.debug("[STOCK_MAPPING] Mapping stock | stockId={} | productId={} | sku={} | quantity={}",
                stock.getId(),
                stock.getProduct().getId(),
                stock.getProduct().getSku(),
                stock.getQuantity());

        return StockResponse.builder()
                .productId(stock.getProduct().getId())
                .sku(stock.getProduct().getSku())
                .productName(stock.getProduct().getProductName())
                .quantity(stock.getQuantity())
                .build();
    }
}
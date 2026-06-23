package com.threepm.api.toko.Service.impl;

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
        log.info("[STOCK_GET] productId={}", productId);

        Stocks stock = stocksRepository.findByProduct_Id(productId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        return toResponse(stock);
    }

    @Override
    public List<StockResponse> getAllStocks() {
        log.info("[STOCK_GET_ALL]");

        return stocksRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private StockResponse toResponse(Stocks stock) {
        return StockResponse.builder()
                .productId(stock.getProduct().getId())
                .sku(stock.getProduct().getSku())
                .productName(stock.getProduct().getProductName())
                .quantity(stock.getQuantity())
                .build();
    }
}
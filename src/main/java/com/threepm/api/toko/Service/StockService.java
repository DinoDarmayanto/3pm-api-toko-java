package com.threepm.api.toko.Service;

import com.threepm.api.toko.Model.Response.StockResponse;

import java.util.List;

public interface StockService {

    StockResponse getStock(Long productId);

    List<StockResponse> getAllStocks();
}
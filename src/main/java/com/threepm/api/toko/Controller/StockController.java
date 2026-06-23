package com.threepm.api.toko.Controller;

import com.threepm.api.toko.Model.Response.StockResponse;
import com.threepm.api.toko.Service.StockService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stocks")
@Tag(name = "Stocks")
@SecurityRequirement(name = "bearerAuth")
public class StockController {

    private final StockService stockService;

    @GetMapping
    public List<StockResponse> getAllStocks() {
        return stockService.getAllStocks();
    }

    @GetMapping("/{productId}")
    public StockResponse getStock(@PathVariable Long productId) {
        return stockService.getStock(productId);
    }
}
package com.threepm.api.toko.Controller;

import com.threepm.api.toko.Model.Request.SaleRequest;
import com.threepm.api.toko.Model.Response.SaleResponse;
import com.threepm.api.toko.Service.SaleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sales")
@Tag(name = "Sales")
@SecurityRequirement(name = "bearerAuth")
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public SaleResponse createSale(@Valid @RequestBody SaleRequest request) {
        return saleService.createSale(request);
    }

    @GetMapping("/{id}")
    public SaleResponse getSale(@PathVariable Long id) {
        return saleService.getSale(id);
    }

    @GetMapping
    public List<SaleResponse> getAllSales() {
        return saleService.getAllSales();
    }
}
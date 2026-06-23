package com.threepm.api.toko.Controller;

import com.threepm.api.toko.Model.Response.ProfitSaleResponse;
import com.threepm.api.toko.Model.Response.TopProfitProductResponse;
import com.threepm.api.toko.Model.Response.TopSellingProductResponse;
import com.threepm.api.toko.Service.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
@Tag(name = "Reports")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/top-selling-products")
    public List<TopSellingProductResponse> getTop5SellingProducts() {
        return reportService.getTop5SellingProducts();
    }

    @GetMapping("/top-profitable-products")
    public List<TopProfitProductResponse> getTop5ProfitableProducts() {
        return reportService.getTop5ProfitableProducts();
    }

    @GetMapping("/sales-containing-top-profitable-products")
    public List<ProfitSaleResponse> getSalesContainingTop5ProfitableProducts() {
        return reportService.getSalesContainingTop5ProfitableProducts();
    }
}
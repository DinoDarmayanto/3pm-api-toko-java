package com.threepm.api.toko.Service.impl;

import com.threepm.api.toko.Model.Entity.SaleDetails;
import com.threepm.api.toko.Model.Entity.Sales;
import com.threepm.api.toko.Model.Response.ProfitSaleResponse;
import com.threepm.api.toko.Model.Response.SaleDetailResponse;
import com.threepm.api.toko.Model.Response.TopProfitProductResponse;
import com.threepm.api.toko.Model.Response.TopSellingProductResponse;
import com.threepm.api.toko.Repository.SaleDetailsRepository;
import com.threepm.api.toko.Repository.SalesRepository;
import com.threepm.api.toko.Service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final SaleDetailsRepository saleDetailsRepository;
    private final SalesRepository salesRepository;

    @Override
    public List<TopSellingProductResponse> getTop5SellingProducts() {
        log.info("[REPORT] Get top 5 selling products");

        return saleDetailsRepository.findTopSellingProducts(PageRequest.of(0, 5))
                .stream()
                .map(row -> TopSellingProductResponse.builder()
                        .productId((Long) row[0])
                        .sku((String) row[1])
                        .productName((String) row[2])
                        .totalQty(((Number) row[3]).intValue())
                        .build())
                .toList();
    }

    @Override
    public List<TopProfitProductResponse> getTop5ProfitableProducts() {
        log.info("[REPORT] Get top 5 profitable products");

        return saleDetailsRepository.findTopProfitableProducts(PageRequest.of(0, 5))
                .stream()
                .map(row -> TopProfitProductResponse.builder()
                        .productId((Long) row[0])
                        .sku((String) row[1])
                        .productName((String) row[2])
                        .qty(((Number) row[3]).intValue())
                        .total((java.math.BigDecimal) row[4])
                        .modal((java.math.BigDecimal) row[5])
                        .keuntungan((java.math.BigDecimal) row[6])
                        .build())
                .toList();
    }

    @Override
    public List<ProfitSaleResponse> getSalesContainingTop5ProfitableProducts() {
        log.info("[REPORT] Get sales containing top 5 profitable products");

        List<Long> productIds = saleDetailsRepository.findTopProfitableProductIds(PageRequest.of(0, 5));

        if (productIds.isEmpty()) {
            return List.of();
        }

        List<Sales> sales = salesRepository.findSalesContainingProducts(productIds);

        return sales.stream()
                .map(sale -> ProfitSaleResponse.builder()
                        .idPenjualan(sale.getId())
                        .transactionNo(sale.getTransactionNo())
                        .tanggal(sale.getSaleDate())
                        .detailBarang(
                                saleDetailsRepository.findBySaleId(sale.getId())
                                        .stream()
                                        .map(this::toSaleDetailResponse)
                                        .toList()
                        )
                        .build())
                .toList();
    }

    private SaleDetailResponse toSaleDetailResponse(SaleDetails detail) {
        return SaleDetailResponse.builder()
                .productId(detail.getProduct().getId())
                .sku(detail.getProduct().getSku())
                .productName(detail.getProduct().getProductName())
                .quantity(detail.getQuantity())
                .purchasePrice(detail.getPurchasePrice())
                .sellingPrice(detail.getSellingPrice())
                .subtotal(detail.getSubtotal())
                .profit(detail.getProfit())
                .build();
    }
}
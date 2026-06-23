package com.threepm.api.toko.Service.impl;

import com.threepm.api.toko.Model.Entity.Products;
import com.threepm.api.toko.Model.Entity.SaleDetails;
import com.threepm.api.toko.Model.Entity.Sales;
import com.threepm.api.toko.Model.Entity.Stocks;
import com.threepm.api.toko.Repository.ProductsRepository;
import com.threepm.api.toko.Repository.SaleDetailsRepository;
import com.threepm.api.toko.Repository.SalesRepository;
import com.threepm.api.toko.Repository.StocksRepository;
import com.threepm.api.toko.Model.Request.SaleDetailRequest;
import com.threepm.api.toko.Model.Request.SaleRequest;
import com.threepm.api.toko.Model.Response.SaleDetailResponse;
import com.threepm.api.toko.Model.Response.SaleResponse;
import com.threepm.api.toko.Service.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleServiceImpl implements SaleService {

    private final SalesRepository salesRepository;
    private final SaleDetailsRepository saleDetailsRepository;
    private final ProductsRepository productsRepository;
    private final StocksRepository stocksRepository;

    @Override
    @Transactional
    public SaleResponse createSale(SaleRequest request) {
        log.info("[SALE_CREATE] Start create sale");

        Sales sale = Sales.builder()
                .transactionNo(generateTransactionNo())
                .saleDate(LocalDateTime.now())
                .totalAmount(BigDecimal.ZERO)
                .build();

        sale = salesRepository.save(sale);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SaleDetailRequest item : request.getItems()) {
            Products product = productsRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            Stocks stock = stocksRepository.findByProduct_Id(product.getId())
                    .orElseThrow(() -> new RuntimeException("Stock not found"));

            if (stock.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
            }

            stock.setQuantity(stock.getQuantity() - item.getQuantity());
            stocksRepository.save(stock);

            BigDecimal qty = BigDecimal.valueOf(item.getQuantity());
            BigDecimal subtotal = product.getSellingPrice().multiply(qty);
            BigDecimal modal = product.getPurchasePrice().multiply(qty);
            BigDecimal profit = subtotal.subtract(modal);

            SaleDetails detail = SaleDetails.builder()
                    .sale(sale)
                    .product(product)
                    .quantity(item.getQuantity())
                    .purchasePrice(product.getPurchasePrice())
                    .sellingPrice(product.getSellingPrice())
                    .subtotal(subtotal)
                    .profit(profit)
                    .build();

            saleDetailsRepository.save(detail);

            totalAmount = totalAmount.add(subtotal);
        }

        sale.setTotalAmount(totalAmount);
        sale = salesRepository.save(sale);

        log.info("[SALE_CREATE] Success transactionNo={}", sale.getTransactionNo());

        return toResponse(sale);
    }

    @Override
    public SaleResponse getSale(Long saleId) {
        Sales sale = salesRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        return toResponse(sale);
    }

    @Override
    public List<SaleResponse> getAllSales() {
        return salesRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private String generateTransactionNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long countToday = salesRepository.countBySaleDateToday(LocalDate.now());
        return "AA" + date + String.format("%04d", countToday + 1);
    }

    private SaleResponse toResponse(Sales sale) {
        List<SaleDetailResponse> details = saleDetailsRepository.findBySaleId(sale.getId())
                .stream()
                .map(this::toSaleDetailResponse)
                .toList();

        return SaleResponse.builder()
                .id(sale.getId())
                .transactionNo(sale.getTransactionNo())
                .saleDate(sale.getSaleDate())
                .totalAmount(sale.getTotalAmount())
                .details(details)
                .build();
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
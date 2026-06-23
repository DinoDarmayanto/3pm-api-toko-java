package com.threepm.api.toko.Service.impl;

import com.threepm.api.toko.Exception.ResourceNotFoundException;
import com.threepm.api.toko.Model.Entity.Products;
import com.threepm.api.toko.Model.Entity.SaleDetails;
import com.threepm.api.toko.Model.Entity.Sales;
import com.threepm.api.toko.Model.Entity.Stocks;
import com.threepm.api.toko.Model.Request.SaleDetailRequest;
import com.threepm.api.toko.Model.Request.SaleRequest;
import com.threepm.api.toko.Model.Response.SaleDetailResponse;
import com.threepm.api.toko.Model.Response.SaleResponse;
import com.threepm.api.toko.Repository.ProductsRepository;
import com.threepm.api.toko.Repository.SaleDetailsRepository;
import com.threepm.api.toko.Repository.SalesRepository;
import com.threepm.api.toko.Repository.StocksRepository;
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
        log.info("[SALE_CREATE] Start | totalItems={}",
                request.getItems() == null ? 0 : request.getItems().size());

        String transactionNo = generateTransactionNo();

        log.info("[SALE_CREATE] Generated transactionNo={}", transactionNo);

        Sales sale = Sales.builder()
                .transactionNo(transactionNo)
                .saleDate(LocalDateTime.now())
                .totalAmount(BigDecimal.ZERO)
                .build();

        sale = salesRepository.save(sale);

        log.info("[SALE_CREATE] Sale header saved | saleId={} | transactionNo={}",
                sale.getId(), sale.getTransactionNo());

        BigDecimal totalAmount = BigDecimal.ZERO;

        int index = 1;

        for (SaleDetailRequest item : request.getItems()) {
            log.info("[SALE_CREATE] Processing item #{} | productId={} | quantity={}",
                    index, item.getProductId(), item.getQuantity());

            Products product = productsRepository.findById(item.getProductId())
                    .orElseThrow(() -> {
                        log.warn("[SALE_CREATE] Failed | product not found | productId={}", item.getProductId());
                        return new ResourceNotFoundException("Product not found with id: " + item.getProductId());
                    });

            log.info(
                    "[SALE_CREATE] Product found | productId={} | sku={} | productName={} | purchasePrice={} | sellingPrice={}",
                    product.getId(),
                    product.getSku(),
                    product.getProductName(),
                    product.getPurchasePrice(),
                    product.getSellingPrice());

            Stocks stock = stocksRepository.findByProduct_Id(product.getId())
                    .orElseThrow(() -> {
                        log.warn("[SALE_CREATE] Failed | stock not found | productId={}", product.getId());
                        return new ResourceNotFoundException("Stock not found for productId: " + product.getId());
                    });

            log.info(
                    "[SALE_CREATE] Stock found | stockId={} | productId={} | currentQuantity={} | requestedQuantity={}",
                    stock.getId(),
                    product.getId(),
                    stock.getQuantity(),
                    item.getQuantity());

            if (stock.getQuantity() < item.getQuantity()) {
                log.warn(
                        "[SALE_CREATE] Failed | insufficient stock | productId={} | currentQuantity={} | requestedQuantity={}",
                        product.getId(), stock.getQuantity(), item.getQuantity());
                throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
            }

            Integer beforeStock = stock.getQuantity();
            Integer afterStock = beforeStock - item.getQuantity();

            stock.setQuantity(afterStock);
            stocksRepository.save(stock);

            log.info("[SALE_CREATE] Stock updated | productId={} | before={} | after={}",
                    product.getId(), beforeStock, afterStock);

            BigDecimal qty = BigDecimal.valueOf(item.getQuantity());
            BigDecimal subtotal = product.getSellingPrice().multiply(qty);
            BigDecimal modal = product.getPurchasePrice().multiply(qty);
            BigDecimal profit = subtotal.subtract(modal);

            log.info("[SALE_CREATE] Calculation | productId={} | qty={} | subtotal={} | modal={} | profit={}",
                    product.getId(), qty, subtotal, modal, profit);

            SaleDetails detail = SaleDetails.builder()
                    .sale(sale)
                    .product(product)
                    .quantity(item.getQuantity())
                    .purchasePrice(product.getPurchasePrice())
                    .sellingPrice(product.getSellingPrice())
                    .subtotal(subtotal)
                    .profit(profit)
                    .build();

            detail = saleDetailsRepository.save(detail);

            log.info("[SALE_CREATE] Sale detail saved | detailId={} | saleId={} | productId={}",
                    detail.getId(), sale.getId(), product.getId());

            totalAmount = totalAmount.add(subtotal);

            log.info("[SALE_CREATE] Running total | saleId={} | totalAmount={}",
                    sale.getId(), totalAmount);

            index++;
        }

        sale.setTotalAmount(totalAmount);
        sale = salesRepository.save(sale);

        log.info("[SALE_CREATE] Success | saleId={} | transactionNo={} | totalAmount={}",
                sale.getId(), sale.getTransactionNo(), sale.getTotalAmount());

        return toResponse(sale);
    }

    @Override
    public SaleResponse getSale(Long saleId) {
        log.info("[SALE_GET] Start | saleId={}", saleId);

        Sales sale = salesRepository.findById(saleId)
                .orElseThrow(() -> {
                    log.warn("[SALE_GET] Failed | sale not found | saleId={}", saleId);
                    return new ResourceNotFoundException("Sale not found with id: " + saleId);
                });

        log.info("[SALE_GET] Success | saleId={} | transactionNo={}",
                sale.getId(), sale.getTransactionNo());

        return toResponse(sale);
    }

    @Override
    public List<SaleResponse> getAllSales() {
        log.info("[SALE_GET_ALL] Start");

        List<Sales> sales = salesRepository.findAll();

        log.info("[SALE_GET_ALL] Data loaded | totalSales={}", sales.size());

        List<SaleResponse> responses = sales.stream()
                .map(this::toResponse)
                .toList();

        log.info("[SALE_GET_ALL] Success | totalResponse={}", responses.size());

        return responses;
    }

    private String generateTransactionNo() {
        LocalDate today = LocalDate.now();
        String date = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long countToday = salesRepository.countBySaleDateToday(today);
        String transactionNo = "AA" + date + String.format("%04d", countToday + 1);

        log.info("[SALE_GENERATE_TRANSACTION_NO] today={} | countToday={} | transactionNo={}",
                today, countToday, transactionNo);

        return transactionNo;
    }

    private SaleResponse toResponse(Sales sale) {
        log.debug("[SALE_MAPPING] Start | saleId={} | transactionNo={}",
                sale.getId(), sale.getTransactionNo());

        List<SaleDetailResponse> details = saleDetailsRepository.findBySaleId(sale.getId())
                .stream()
                .map(this::toSaleDetailResponse)
                .toList();

        log.debug("[SALE_MAPPING] Details loaded | saleId={} | totalDetails={}",
                sale.getId(), details.size());

        return SaleResponse.builder()
                .id(sale.getId())
                .transactionNo(sale.getTransactionNo())
                .saleDate(sale.getSaleDate())
                .totalAmount(sale.getTotalAmount())
                .details(details)
                .build();
    }

    private SaleDetailResponse toSaleDetailResponse(SaleDetails detail) {
        log.debug("[SALE_DETAIL_MAPPING] detailId={} | productId={} | quantity={}",
                detail.getId(), detail.getProduct().getId(), detail.getQuantity());

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
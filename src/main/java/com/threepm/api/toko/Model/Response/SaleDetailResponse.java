package com.threepm.api.toko.Model.Response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleDetailResponse {

    private Long productId;

    private String sku;

    private String productName;

    private Integer quantity;

    private BigDecimal purchasePrice;

    private BigDecimal sellingPrice;

    private BigDecimal subtotal;

    private BigDecimal profit;
}
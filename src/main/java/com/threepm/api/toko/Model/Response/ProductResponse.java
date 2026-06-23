package com.threepm.api.toko.Model.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;

    private String sku;

    private String productName;

    private BigDecimal purchasePrice;

    private BigDecimal sellingPrice;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
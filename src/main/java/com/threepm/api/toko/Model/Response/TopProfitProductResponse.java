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
public class TopProfitProductResponse {

    private Long productId;

    private String sku;

    private String productName;

    private Integer qty;

    private BigDecimal total;

    private BigDecimal modal;

    private BigDecimal keuntungan;
}
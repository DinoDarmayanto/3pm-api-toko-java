package com.threepm.api.toko.Model.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponse {

    private Long id;

    private String transactionNo;

    private LocalDateTime saleDate;

    private BigDecimal totalAmount;

    private List<SaleDetailResponse> details;
}
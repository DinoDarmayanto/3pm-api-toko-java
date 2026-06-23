package com.threepm.api.toko.Model.Response;

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
public class ProfitSaleResponse {

    private Long idPenjualan;

    private String transactionNo;

    private LocalDateTime tanggal;

    private List<SaleDetailResponse> detailBarang;
}
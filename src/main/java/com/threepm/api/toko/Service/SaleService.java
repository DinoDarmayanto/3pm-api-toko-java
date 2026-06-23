package com.threepm.api.toko.Service;

import com.threepm.api.toko.Model.Request.SaleRequest;
import com.threepm.api.toko.Model.Response.SaleResponse;

import java.util.List;

public interface SaleService {

    SaleResponse createSale(SaleRequest request);

    SaleResponse getSale(Long saleId);

    List<SaleResponse> getAllSales();
}
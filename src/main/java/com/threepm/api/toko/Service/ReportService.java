package com.threepm.api.toko.Service;

import com.threepm.api.toko.Model.Response.ProfitSaleResponse;
import com.threepm.api.toko.Model.Response.TopProfitProductResponse;
import com.threepm.api.toko.Model.Response.TopSellingProductResponse;

import java.util.List;

public interface ReportService {

    List<TopSellingProductResponse> getTop5SellingProducts();

    List<TopProfitProductResponse> getTop5ProfitableProducts();

    List<ProfitSaleResponse> getSalesContainingTop5ProfitableProducts();
}
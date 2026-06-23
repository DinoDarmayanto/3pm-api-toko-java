package com.threepm.api.toko.Service;

import com.threepm.api.toko.Model.Request.ProductRequest;
import com.threepm.api.toko.Model.Response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    ProductResponse findById(Long id);

    List<ProductResponse> findAll();

    void delete(Long id);
}
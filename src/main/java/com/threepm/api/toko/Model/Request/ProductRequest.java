package com.threepm.api.toko.Model.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank
    private String sku;

    @NotBlank
    private String productName;

    @NotNull
    @Positive
    private BigDecimal purchasePrice;

    @NotNull
    @Positive
    private BigDecimal sellingPrice;

    private String description;
}
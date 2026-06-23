package com.threepm.api.toko.Repository;

import com.threepm.api.toko.Model.Entity.SaleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface SaleDetailsRepository extends JpaRepository<SaleDetails, Long> {

    List<SaleDetails> findBySaleId(Long saleId);

    List<SaleDetails> findByProductId(Long productId);

    @Query("""
            SELECT sd.product.id,
                   sd.product.sku,
                   sd.product.productName,
                   SUM(sd.quantity)
            FROM SaleDetails sd
            GROUP BY sd.product.id, sd.product.sku, sd.product.productName
            ORDER BY SUM(sd.quantity) DESC
            """)
    List<Object[]> findTopSellingProducts(Pageable pageable);

    @Query("""
            SELECT sd.product.id,
                   sd.product.sku,
                   sd.product.productName,
                   SUM(sd.quantity),
                   SUM(sd.subtotal),
                   SUM(sd.purchasePrice * sd.quantity),
                   SUM(sd.profit)
            FROM SaleDetails sd
            GROUP BY sd.product.id, sd.product.sku, sd.product.productName
            ORDER BY SUM(sd.profit) DESC
            """)
    List<Object[]> findTopProfitableProducts(Pageable pageable);

    @Query("""
            SELECT sd.product.id
            FROM SaleDetails sd
            GROUP BY sd.product.id
            ORDER BY SUM(sd.profit) DESC
            """)
    List<Long> findTopProfitableProductIds(Pageable pageable);
}
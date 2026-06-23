package com.threepm.api.toko.Repository;

import com.threepm.api.toko.Model.Entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {

    Optional<Products> findBySku(String sku);

    boolean existsBySku(String sku);

    @Query("""
            SELECT sd.product.id,
                   SUM(sd.quantity)
            FROM SaleDetails sd
            GROUP BY sd.product.id
            ORDER BY SUM(sd.quantity) DESC
            """)
    List<Object[]> findTopSellingProducts(Pageable pageable);

    @Query("""
            SELECT sd.product.id,
                   SUM(sd.profit)
            FROM SaleDetails sd
            GROUP BY sd.product.id
            ORDER BY SUM(sd.profit) DESC
            """)
    List<Object[]> findTopProfitableProducts(Pageable pageable);
}
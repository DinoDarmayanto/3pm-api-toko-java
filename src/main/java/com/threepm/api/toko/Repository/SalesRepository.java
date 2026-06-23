package com.threepm.api.toko.Repository;

import com.threepm.api.toko.Model.Entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import java.util.Optional;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {

    Optional<Sales> findByTransactionNo(String transactionNo);

    boolean existsByTransactionNo(String transactionNo);

    @Query("""
            SELECT COUNT(s)
            FROM Sales s
            WHERE DATE(s.saleDate) = :date
            """)
    long countBySaleDateToday(@Param("date") LocalDate date);

    @Query("""
            SELECT DISTINCT s
            FROM Sales s
            JOIN SaleDetails sd ON sd.sale = s
            WHERE sd.product.id IN :productIds
            ORDER BY s.saleDate DESC
            """)
    List<Sales> findSalesContainingProducts(@Param("productIds") List<Long> productIds);
}
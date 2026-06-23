package com.threepm.api.toko.Repository;

import com.threepm.api.toko.Model.Entity.Stocks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StocksRepository extends JpaRepository<Stocks, Long> {

    Optional<Stocks> findByProduct_Id(Long productId);
}
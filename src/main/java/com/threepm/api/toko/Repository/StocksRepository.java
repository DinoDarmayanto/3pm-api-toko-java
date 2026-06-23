package com.threepm.api.toko.Repository;

import com.threepm.api.toko.Model.Entity.Stocks;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StocksRepository extends JpaRepository<Stocks, Long> {

    @EntityGraph(attributePaths = "product")
    Optional<Stocks> findByProduct_Id(Long productId);

    @Override
    @EntityGraph(attributePaths = "product")
    List<Stocks> findAll();
}
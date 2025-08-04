package com.reddy.finance_dashboard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reddy.finance_dashboard.entity.Holding;
import com.reddy.finance_dashboard.entity.Portfolio;
import com.reddy.finance_dashboard.entity.Stock;

public interface HoldingRepository extends JpaRepository<Holding, Long> {

    Optional<Holding> findByPortfolioAndStock(Portfolio portfolio, Stock stock);

    List<Holding> findByPortfolio(Portfolio portfolio);
}

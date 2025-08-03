package com.reddy.finance_dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reddy.finance_dashboard.entity.TradeOrder;

public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {
}

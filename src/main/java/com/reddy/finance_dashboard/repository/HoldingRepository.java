package com.reddy.finance_dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reddy.finance_dashboard.entity.Holding;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
}

package com.reddy.finance_dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reddy.finance_dashboard.entity.P2PTransfer;

public interface P2PTransferRepository extends JpaRepository<P2PTransfer, Long> {
}

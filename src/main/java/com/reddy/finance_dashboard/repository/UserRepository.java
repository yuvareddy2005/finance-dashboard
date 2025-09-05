package com.reddy.finance_dashboard.repository;

import java.util.Optional; // <-- ADD THIS IMPORT

import org.springframework.data.jpa.repository.JpaRepository;

import com.reddy.finance_dashboard.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring Data JPA will automatically create the query for this method
    // based on the method name.
    Optional<User> findByEmail(String email);

}
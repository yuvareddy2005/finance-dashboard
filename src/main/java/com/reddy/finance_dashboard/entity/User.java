package com.reddy.finance_dashboard.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails { // <-- IMPLEMENT THE INTERFACE

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;

    // v-- ADD THESE REQUIRED METHODS FROM UserDetails --v

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // For now, we are not using roles, so we return an empty list.
        // We will add roles (like USER, ADMIN) later.
        return List.of();
    }

    @Override
    public String getUsername() {
        // Our "username" is the user's email address.
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Account is never expired
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Account is never locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Credentials are never expired
    }

    @Override
    public boolean isEnabled() {
        return true; // Account is always enabled
    }
}
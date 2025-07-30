package com.reddy.finance_dashboard.config;

// We are removing the @Autowired fields here

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // We removed the @Autowired fields for JwtAuthenticationFilter and AuthenticationProvider

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        AuthenticationProvider authenticationProvider, // <-- Inject here instead
        JwtAuthenticationFilter jwtAuthFilter         // <-- Inject here instead
    ) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider) // Use the injected provider
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Use the injected filter

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
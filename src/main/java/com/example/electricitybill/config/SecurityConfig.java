package com.example.electricitybill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/users/findbytoken2",
            "/api/users/findtoken",
            "/api/users/add",
            "/api/users/login",
            "/api/users/findbyphonenum",
            "/api/users/delete/{id}",
            "/api/users/allusers",
            "/api/users/logout",
            "/api/users/grantAdmin",
            "/api/users/revokeAdmin",
            "/api/users/admin/logout",
            "/api/users/update/{phoneNumber}",
            "/api/bills/add",
            "/api/bills/find/all",
            "/api/electricity-rate/*"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(authorize -> authorize
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
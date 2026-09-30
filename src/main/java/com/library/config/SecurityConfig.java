package com.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // ==============================
                        // AUTHENTICATION
                        // ==============================
                        .requestMatchers(
                                "/api/auth/send-otp",
                                "/api/auth/verify-otp"
                        ).permitAll()

                        // ==============================
                        // BOOKS - MEMBER + ADMIN READ
                        // ==============================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/books",
                                "/api/books/**"
                        ).hasAnyAuthority(
                                "ROLE_ADMIN",
                                "ROLE_MEMBER"
                        )

                        // ==============================
                        // BOOKS - ADMIN WRITE
                        // ==============================
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/books",
                                "/api/books/**"
                        ).hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/books",
                                "/api/books/**"
                        ).hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/books",
                                "/api/books/**"
                        ).hasAuthority("ROLE_ADMIN")

                        // ==============================
                        // MEMBERS - ADMIN ONLY
                        // ==============================
                        .requestMatchers(
                                "/api/members",
                                "/api/members/**"
                        ).hasAuthority("ROLE_ADMIN")

                        // ==============================
                        // MEMBER TRANSACTIONS
                        // ==============================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/transactions/my",
                                "/api/transactions/my/**"
                        ).hasAnyAuthority(
                                "ROLE_ADMIN",
                                "ROLE_MEMBER"
                        )

                        // ==============================
                        // ALL OTHER TRANSACTIONS
                        // ADMIN ONLY
                        // ==============================
                        .requestMatchers(
                                "/api/transactions",
                                "/api/transactions/**"
                        ).hasAuthority("ROLE_ADMIN")

                        // ==============================
                        // MEMBER FINES
                        // ==============================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/fines/my"
                        ).hasAnyAuthority(
                                "ROLE_ADMIN",
                                "ROLE_MEMBER"
                        )

                        // ==============================
                        // ALL OTHER FINES
                        // ADMIN ONLY
                        // ==============================
                        .requestMatchers(
                                "/api/fines",
                                "/api/fines/**"
                        ).hasAuthority("ROLE_ADMIN")

                        // ==============================
                        // REPORTS - ADMIN ONLY
                        // ==============================
                        .requestMatchers(
                                "/api/reports",
                                "/api/reports/**"
                        ).hasAuthority("ROLE_ADMIN")

                        // ==============================
                        // TEMPORARY JWT TEST
                        // ==============================
                        .requestMatchers(
                                "/api/test"
                        ).authenticated()

                        // ==============================
                        // EVERYTHING ELSE
                        // ==============================
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
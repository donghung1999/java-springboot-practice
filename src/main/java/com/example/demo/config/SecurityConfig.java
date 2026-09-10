package com.example.demo.config;

import com.example.demo.security.IpWhitelistFilter;
import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.security.RateLimitFilter;
import com.example.demo.security.RequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitFilter rateLimitFilter;
    private final IpWhitelistFilter ipWhitelistFilter;
    private final RequestLoggingFilter requestLoggingFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, RateLimitFilter rateLimitFilter, IpWhitelistFilter ipWhitelistFilter,
                          RequestLoggingFilter requestLoggingFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.rateLimitFilter = rateLimitFilter;
        this.ipWhitelistFilter = ipWhitelistFilter;
        this.requestLoggingFilter = requestLoggingFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/**",
                    "/swagger",
                    "/swagger-ui/**",
                    "/api-docs/**"
                ).permitAll()
                // USER hoặc ADMIN
                .requestMatchers("/users/**")
                .hasAnyRole("USER", "ADMIN")

                .anyRequest().authenticated()
            )

            .addFilterBefore(
                this.requestLoggingFilter,
                UsernamePasswordAuthenticationFilter.class
            )

            .addFilterAfter(
                this.ipWhitelistFilter,
                RequestLoggingFilter.class
            )

            .addFilterAfter(
                this.rateLimitFilter,
                IpWhitelistFilter.class
            )

            .addFilterAfter(
                this.jwtAuthenticationFilter,
                RateLimitFilter.class
            );

        return http.build();
    }
}

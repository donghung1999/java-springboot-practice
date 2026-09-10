package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;
            int status = response.getStatus();
            String statusColor;

            if (status >= 200 && status < 300) {
                statusColor = GREEN;
            } else if (status >= 400 && status < 500) {
                statusColor = YELLOW;
            } else {
                statusColor = RED;
            }

            log.info(
                    CYAN + "{} {}" + RESET
                            + " | Status=" + statusColor + "{}" + RESET
                            + " | IP={}"
                            + " | Duration={}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    request.getRemoteAddr(),
                    duration
            );
        }
    }
}

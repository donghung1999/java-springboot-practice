package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class IpWhitelistFilter extends OncePerRequestFilter {
    private final Set<String> allowedIps = Set.of(
            "127.0.0.1",
            "192.168.1.10",
            "10.0.0.1"
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        if (!this.allowedIps.contains(ip)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                    "status": 403,
                    "message": "IP address is not allowed"
                }
                """);

            return;
        }

        filterChain.doFilter(request, response);
    }
}

package com.example.demo.controller;

import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.security.JwtService;
import com.example.demo.service.AuthService;
import com.example.demo.service.RedisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final RedisService redisService;

    public AuthController(AuthService authService, JwtService jwtService, RedisService redisService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.redisService = redisService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginDTO dto) {
        return authService.login(dto);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Authorization header không hợp lệ"
            );
        }

        String token = authorization.substring(7);
        long remainingExpiration = this.jwtService.getRemainingExpiration(token);
        if (remainingExpiration > 0) {
            this.redisService.blacklistToken(
                token,
                remainingExpiration
            );
        }

        return ResponseEntity.ok(
            Map.of(
                "message", "Logout successful"
            )
        );
    }
}

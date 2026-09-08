package com.example.demo.service;

import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO login(LoginDTO dto) {
        var user = this.userRepository.findByUsername(dto.getUsername()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Username không tồn tại")
        );

        boolean valid = passwordEncoder.matches(
                dto.getPassword(),
                user.getPassword()
        );
        if (!valid) {
            throw new RuntimeException("Password không đúng");
        }

        String token = jwtService.generateToken(user);
        return new LoginResponseDTO(token);
    }
}

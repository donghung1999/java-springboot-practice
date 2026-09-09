package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public UserDTO register(UserRegisterDTO userDTO) {
        Role userRole = roleRepository.findByName("USER").orElseThrow(() ->
            new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Default role USER không tồn tại"
            )
        );

        if (this.userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Username đã tồn tại"
            );
        }

        if (this.userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email đã tồn tại"
            );
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        /*
        boolean valid = passwordEncoder.matches(
            dto.getPassword(),
            user.getPassword()
        );

        if (!valid) {
            throw new RuntimeException("Invalid password");
        }
        * */
        // pass: 123456
        var hashedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(hashedPassword);
        user.setRole(userRole);
        this.userRepository.save(user);
        return new UserDTO(userDTO.getUsername(), userDTO.getName(), userDTO.getEmail());
    }
}

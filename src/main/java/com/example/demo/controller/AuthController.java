package com.example.demo.controller;

import com.example.demo.dto.AuthRequestDto;
import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.repository.SystemUserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final SystemUserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, SystemUserRepository userRepository, JwtUtil jwtUtil) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto request) {
        SystemUser user = authService.login(request.getUsername(), request.getPassword());
        String token = generateTokenFor(user);

        AuthResponseDto response = new AuthResponseDto(
                user.getId(),
                token,
                user.getUsername(),
                user.getRole().name()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto request) {
        SystemUser.UserRole role = SystemUser.UserRole.valueOf(request.getRole());
        SystemUser user = authService.register(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getFullName(),
                role
        );
        String token = generateTokenFor(user);

        AuthResponseDto response = new AuthResponseDto(
                user.getId(),
                token,
                user.getUsername(),
                user.getRole().name()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HOSPITALITY_ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully.");
    }

    private String generateTokenFor(SystemUser user) {
        UserDetails userDetails = User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
        return jwtUtil.generateToken(userDetails, user.getRole().name());
    }
}

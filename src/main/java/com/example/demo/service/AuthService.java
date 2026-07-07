package com.example.demo.service;

import com.example.demo.entity.SystemUser;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.repository.SystemUserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final SystemUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(SystemUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public SystemUser register(String username, String password, String email,
                               String fullName, SystemUser.UserRole role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BusinessValidationException("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessValidationException("Email already exists");
        }
        SystemUser user = new SystemUser(
                username,
                passwordEncoder.encode(password),
                email,
                fullName,
                role
        );
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            // Safety net for race conditions or any other unique-constraint
            // violation not caught by the checks above.
            String reason = ex.getMostSpecificCause() != null
                    ? ex.getMostSpecificCause().getMessage()
                    : ex.getMessage();
            if (reason != null && reason.toLowerCase().contains("email")) {
                throw new BusinessValidationException("Email already exists");
            }
            if (reason != null && reason.toLowerCase().contains("username")) {
                throw new BusinessValidationException("Username already exists");
            }
            throw new BusinessValidationException("Registration failed due to duplicate data");
        }
    }

    public SystemUser login(String username, String password) {
        SystemUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return user;
    }
}

package com.example.demo;

import com.example.demo.entity.SystemUser;
import com.example.demo.repository.SystemUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(SystemUserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(new SystemUser(
                        "admin",
                        passwordEncoder.encode("admin123"),
                        "admin@hosthaven.com",
                        "System Admin",
                        SystemUser.UserRole.HOSPITALITY_ADMIN
                ));
                userRepository.save(new SystemUser(
                        "owner",
                        passwordEncoder.encode("owner123"),
                        "owner@hosthaven.com",
                        "Property Owner",
                        SystemUser.UserRole.PROPERTY_OWNER
                ));
                userRepository.save(new SystemUser(
                        "guest",
                        passwordEncoder.encode("guest123"),
                        "guest@hosthaven.com",
                        "Regular Guest",
                        SystemUser.UserRole.GUEST
                ));
                userRepository.save(new SystemUser(
                        "mani",
                        passwordEncoder.encode("mani123"),
                        "mani@hosthaven.com",
                        "Mani Kumar",
                        SystemUser.UserRole.GUEST
                ));
            }
        };
    }
}

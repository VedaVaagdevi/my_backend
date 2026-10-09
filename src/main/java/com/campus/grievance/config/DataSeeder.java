package com.campus.grievance.config;

import com.campus.grievance.model.Role;
import com.campus.grievance.model.User;
import com.campus.grievance.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedAdmin(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (users.findByEmail("admin@campus.com").isEmpty()) {
                User admin = new User();
                admin.setName("Administrator");
                admin.setEmail("admin@campus.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                users.save(admin);
            }
        };
    }
}
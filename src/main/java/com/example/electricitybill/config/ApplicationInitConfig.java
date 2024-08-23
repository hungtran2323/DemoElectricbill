package com.example.electricitybill.config;

import com.example.electricitybill.model.User;
import com.example.electricitybill.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class ApplicationInitConfig {

    @Bean
    public ApplicationRunner applicationRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> createAdminAccountIfNotExists(userRepository, passwordEncoder);
    }

    private void createAdminAccountIfNotExists(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        Optional<User> userOptional = userRepository.findByPhoneNumber("8888888888");
        if (userOptional.isEmpty()) {
            User user = createAdminUser(passwordEncoder);
            userRepository.save(user);
            System.out.println("Admin account created: " + user);
        } else {
            System.out.println("Admin account already exists.");
        }
    }

    private User createAdminUser(PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setPhoneNumber("8888888888");
        user.setEmail("admin@example.com");
        user.setPassword(passwordEncoder.encode("admin123456"));
        user.setRole("admin");
        return user;
    }
}
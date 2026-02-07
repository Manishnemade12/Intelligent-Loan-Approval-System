package com.loanapproval.config;

import com.loanapproval.common.enums.UserRole;
import com.loanapproval.entity.User;
import com.loanapproval.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
    }

    private void initializeUsers() {
        // Create default users if they don't exist
        if (!userRepository.existsByEmail("customer@example.com")) {
            User customer = User.builder()
                    .name("John Doe")
                    .email("customer@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(UserRole.CUSTOMER)
                    .active(true)
                    .build();
            userRepository.save(customer);
            log.info("Created customer user: customer@example.com");
        }

        if (!userRepository.existsByEmail("officer@example.com")) {
            User officer = User.builder()
                    .name("Sarah Johnson")
                    .email("officer@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(UserRole.OFFICER)
                    .active(true)
                    .build();
            userRepository.save(officer);
            log.info("Created officer user: officer@example.com");
        }

        if (!userRepository.existsByEmail("admin@example.com")) {
            User admin = User.builder()
                    .name("Admin User")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(UserRole.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
            log.info("Created admin user: admin@example.com");
        }
    }
}

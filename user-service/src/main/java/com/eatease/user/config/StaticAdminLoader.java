package com.eatease.user.config;

import com.eatease.common.constants.Role;
import com.eatease.user.entity.User;
import com.eatease.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

/**
 * Creates the single static admin user if not present.
 * Admin authenticates/approves customers and restaurant owners when they register.
 */
@Configuration
public class StaticAdminLoader {

    private static final Logger log = LoggerFactory.getLogger(StaticAdminLoader.class);
    private static final String DEFAULT_ADMIN_EMAIL = "admin@eatease.com";
    private static final String DEFAULT_ADMIN_NAME = "EatEase Admin";

    @Value("${app.admin.email:" + DEFAULT_ADMIN_EMAIL + "}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Bean
    ApplicationRunner initStaticAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPasswordHash(passwordEncoder.encode(adminPassword));
                admin.setName(DEFAULT_ADMIN_NAME);
                admin.setRoles(Set.of(Role.ADMIN));
                admin.setEnabled(true);
                admin.setApproved(true);

                userRepository.save(admin);
                log.info("Static admin user created: {}", adminEmail);
            }
        };
    }
}

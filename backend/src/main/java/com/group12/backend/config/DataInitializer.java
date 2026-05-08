package com.group12.backend.config;

import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.group12.backend.entity.User;
import com.group12.backend.repository.UserRepository;
import com.group12.backend.service.BillingService;
import com.group12.backend.service.LegacyScooterTypeRepairService;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String ADMIN_EMAIL = "admin@admin.com";
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BillingService billingService;
    private final LegacyScooterTypeRepairService legacyScooterTypeRepairService;
    private final String configuredPassword;

    public DataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            BillingService billingService,
            LegacyScooterTypeRepairService legacyScooterTypeRepairService,
            @Value("${admin.initial-password:}") String configuredPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.billingService = billingService;
        this.legacyScooterTypeRepairService = legacyScooterTypeRepairService;
        this.configuredPassword = configuredPassword;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail(ADMIN_EMAIL).isEmpty()) {
            String rawPassword = (configuredPassword != null && !configuredPassword.isBlank())
                    ? configuredPassword
                    : generateRandomPassword(16);

            User admin = new User();
            admin.setEmail(ADMIN_EMAIL);
            admin.setName("Admin");
            admin.setPassword(passwordEncoder.encode(rawPassword));
            admin.setRole("ADMIN");
            admin.setIsStudent(false);
            admin.setAge(25);
            userRepository.save(admin);
            log.info("==========================================================");
            log.info("Default admin account created.");
            log.info("Email: {}", ADMIN_EMAIL);
            log.info("Password: {} (set ADMIN_INITIAL_PASSWORD env var to control this)", rawPassword);
            log.info("==========================================================");
        }
        billingService.getCurrentRule();
        legacyScooterTypeRepairService.repairLegacyGen1Types();
    }

    private static String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}

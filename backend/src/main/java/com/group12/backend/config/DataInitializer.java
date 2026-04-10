package com.group12.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.group12.backend.entity.User;
import com.group12.backend.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@admin.com";
        // 检查是否已经存在该管理员账号，不存在则创建
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setName("Admin");
            // 使用项目自带的密码编码器加密密码
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setIsStudent(false);
            admin.setAge(25);
            userRepository.save(admin);
            System.out.println("==========================================================");
            System.out.println("默认管理员账号已成功注入！");
            System.out.println("账号: " + adminEmail);
            System.out.println("密码: admin123");
            System.out.println("==========================================================");
        }
    }
}

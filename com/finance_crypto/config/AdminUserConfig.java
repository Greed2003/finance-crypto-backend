package com.finance_crypto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.finance_crypto.entity.User;
import com.finance_crypto.repository.UserRepository;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AdminUserConfig(UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        var userAdmin = userRepository.findByUsername("admin");

        userAdmin.ifPresentOrElse(user -> {
            System.out.println("Admin user already exists");
        }, () -> {
            var user = new User();
            user.setUsername("admin");
            user.setEmail("admin@financecrypto.com");
            user.setPassword(bCryptPasswordEncoder.encode("admin"));
            user.setRole("ROLE_ADMIN");
            userRepository.save(user);
        });
    }
}

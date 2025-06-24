package com.doubtapp.backend.config;

import com.doubtapp.backend.model.User;
import com.doubtapp.backend.model.UserRole;
import com.doubtapp.backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, EntityManager entityManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.findByRole(UserRole.ADMIN).isEmpty()) {
            User admin = new User();

            // Find next available ID
            Query idQuery = entityManager.createNativeQuery("SELECT COALESCE(MAX(t1.id), 0) + 1 FROM user t1");

            Long nextId = ((Number) idQuery.getSingleResult()).longValue();
            admin.setId(nextId);

            admin.setEmail("admin@admin.com");
            admin.setName("Admin User");
            admin.setRole(UserRole.ADMIN);
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setUid(UUID.randomUUID().toString());
            userRepository.save(admin);
            System.out.println("Created admin user: admin@admin.com with ID: " + nextId);
        }
    }
} 
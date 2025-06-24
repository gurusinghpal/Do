package com.doubtapp.backend.controller;

import com.doubtapp.backend.model.User;
import com.doubtapp.backend.model.UserRole;
import com.doubtapp.backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/test")
public class TestController {

    private final UserRepository userRepo;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    public TestController(UserRepository userRepo, EntityManager entityManager, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/all-users")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        List<User> users = userRepo.findByRole(userRole);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        logger.info("Checking user with email: {}", email);
        User user = userRepo.findByEmail(email);
        if (user == null) {
            logger.info("No user found with email: {}", email);
            return ResponseEntity.ok("No user found with email: " + email);
        }
        logger.info("Found user: {}", user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        logger.info("Creating user: {}", user);
        User existing = userRepo.findByEmail(user.getEmail());
        if (existing != null) {
            logger.info("User already exists: {}", existing);
            return ResponseEntity.ok(existing);
        }

        // Find next available ID
        Query idQuery = entityManager.createNativeQuery("SELECT COALESCE(MAX(t1.id), 0) + 1 FROM user t1");

        Long nextId = ((Number) idQuery.getSingleResult()).longValue();
        user.setId(nextId);

        // Set a unique ID
        user.setUid(UUID.randomUUID().toString());

        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepo.save(user);
        logger.info("Created new user: {}", savedUser);
        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();
        if (user.getRole() == UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admins cannot be deleted.");
        }

        userRepo.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}

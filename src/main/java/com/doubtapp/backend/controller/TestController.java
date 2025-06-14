package com.doubtapp.backend.controller;

import com.doubtapp.backend.model.User;
import com.doubtapp.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/test")
public class TestController {

    private final UserRepository userRepo;
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    public TestController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/all-users")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        List<User> users = userRepo.findByRole(role.toLowerCase());
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
        User savedUser = userRepo.save(user);
        logger.info("Created new user: {}", savedUser);
        return ResponseEntity.ok(savedUser);
    }
}

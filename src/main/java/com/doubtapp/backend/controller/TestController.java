package com.doubtapp.backend.controller;

import com.doubtapp.backend.model.User;
import com.doubtapp.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/test")
public class TestController {

    private final UserRepository userRepo;

    public TestController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("/add-user")
    public User addUser(@RequestBody User user) {
        return userRepo.save(user);
    }

    @GetMapping("/all-users")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User existing = userRepo.findByUid(user.getUid());
        if (existing != null) {
            return ResponseEntity.ok(existing);
        }
        User savedUser = userRepo.save(user);
        return ResponseEntity.ok(savedUser);
    }
}

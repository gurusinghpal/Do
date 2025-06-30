package com.doubtapp.backend.controller;

import com.doubtapp.backend.model.User;
import com.doubtapp.backend.model.UserRole;
import com.doubtapp.backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Controller
public class HomeController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    public HomeController(UserRepository userRepository, PasswordEncoder passwordEncoder, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority auth : authentication.getAuthorities()) {
                String role = auth.getAuthority();
                if ("ROLE_STUDENT".equals(role)) {
                    return "redirect:/student/dashboard";
                } else if ("ROLE_MENTOR".equals(role)) {
                    return "redirect:/mentor/dashboard";
                } else if ("ROLE_ADMIN".equals(role)) {
                    // Placeholder for admin dashboard
                    return "redirect:/admin/dashboard"; 
                }
            }
        }
        // Fallback to home page if no specific role is found
        return "redirect:/";
    }

    @PostMapping("/register")
    public String handleRegistration(@RequestParam String name,
                                     @RequestParam String email,
                                     @RequestParam String password,
                                     @RequestParam UserRole role) {

        if (userRepository.findByEmail(email) != null) {
            // Optional: Add error handling for existing user
            return "redirect:/register?error";
        }

        User newUser = new User();

        // Find next available ID
        Query idQuery = entityManager.createNativeQuery("SELECT COALESCE(MAX(t1.id), 0) + 1 FROM user t1");
        Long nextId = ((Number) idQuery.getSingleResult()).longValue();
        newUser.setId(nextId);

        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(role);
        newUser.setUid(UUID.randomUUID().toString());

        userRepository.save(newUser);

        return "redirect:/login";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
} 
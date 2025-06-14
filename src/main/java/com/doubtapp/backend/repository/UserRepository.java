package com.doubtapp.backend.repository;

import com.doubtapp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUid(String uid);  // 🔍 useful for checking existing user
    List<User> findByRole(String role);  // Find users by role
    User findByEmail(String email);  // Find user by email
    User findByEmailAndRole(String email, String role);  // Find user by email and role
}
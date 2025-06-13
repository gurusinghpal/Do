package com.doubtapp.backend.repository;

import com.doubtapp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUid(String uid);  // 🔍 useful for checking existing user
}
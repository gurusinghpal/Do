package com.doubtapp.backend.repository;

import com.doubtapp.backend.model.Doubt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoubtRepository extends JpaRepository<Doubt, Long> {
    List<Doubt> findByStudentEmail(String email);
    List<Doubt> findByMentorEmail(String email);
    List<Doubt> findByStatus(String status);
}

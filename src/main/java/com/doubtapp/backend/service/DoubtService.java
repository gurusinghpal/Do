package com.doubtapp.backend.service;

import com.doubtapp.backend.model.Doubt;
import com.doubtapp.backend.repository.DoubtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoubtService {

    @Autowired
    private DoubtRepository doubtRepository;

    // ✅ Student posts a new doubt
    public Doubt postDoubt(Doubt doubt) {
        doubt.setStatus("pending");
        return doubtRepository.save(doubt);
    }

    // ✅ Admin or Mentor can view all doubts
    public List<Doubt> getAllDoubts() {
        return doubtRepository.findAll();
    }

    // ✅ Get all doubts posted by a particular student
    public List<Doubt> getDoubtsByStudent(String email) {
        return doubtRepository.findByStudentEmail(email);
    }

    // ✅ Get all doubts assigned to or answered by a mentor
    public List<Doubt> getDoubtsByMentor(String email) {
        return doubtRepository.findByMentorEmail(email);
    }
    // Get doubt by status
    public List<Doubt> getDoubtsByStatus(String status) {
        return doubtRepository.findByStatus(status);
    }


    // ✅ Mentor answers a doubt
    public Doubt answerDoubt(Long id, String answer, String mentorEmail) {
        Doubt doubt = doubtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doubt not found"));

        doubt.setAnswer(answer);
        doubt.setStatus("answered");
        doubt.setMentorEmail(mentorEmail);

        return doubtRepository.save(doubt);
    }
}

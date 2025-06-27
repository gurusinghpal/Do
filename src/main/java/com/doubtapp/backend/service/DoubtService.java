package com.doubtapp.backend.service;

import com.doubtapp.backend.dto.MentorUpdateRequest;
import com.doubtapp.backend.dto.StudentUpdateRequest;
import com.doubtapp.backend.model.Doubt;
import com.doubtapp.backend.model.User;
import com.doubtapp.backend.model.UserRole;
import com.doubtapp.backend.repository.DoubtRepository;
import com.doubtapp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.Date;
import java.util.Optional;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

@Service
public class DoubtService {
    private static final Logger logger = LoggerFactory.getLogger(DoubtService.class);

    @Autowired
    private DoubtRepository doubtRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private EntityManager entityManager;

    // ✅ Student posts a new doubt
    @Transactional
    public Doubt postDoubt(Doubt doubt) {
        logger.info("Attempting to post doubt: {}", doubt);

        // Validate student email
        if (doubt.getStudentEmail() == null || doubt.getStudentEmail().trim().isEmpty()) {
            throw new RuntimeException("Student email is required");
        }

        // If mentor is assigned, validate mentor email and existence
        if (doubt.getMentorEmail() != null && !doubt.getMentorEmail().trim().isEmpty()) {
            // Validate email format
            if (!doubt.getMentorEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new RuntimeException("Invalid mentor email format");
            }
            
            // Check if mentor exists and is actually a mentor
            Optional<User> mentorOpt = Optional.ofNullable(userRepository.findByEmail(doubt.getMentorEmail()));
            if (mentorOpt.isEmpty()) {
                throw new RuntimeException("Mentor not found");
            }
            
            User mentor = mentorOpt.get();
            if (mentor.getRole() != UserRole.MENTOR) {
                throw new RuntimeException("The specified user is not a mentor");
            }
        }

        // Validate student email
        logger.info("Validating student email: {}", doubt.getStudentEmail());
        User student = userRepository.findByEmail(doubt.getStudentEmail());
        logger.info("Found user by email: {}", student);

        if (student == null) {
            logger.error("No user found with email: {}", doubt.getStudentEmail());
            throw new RuntimeException("Invalid student email. Only registered students can post doubts.");
        }

        // Ensure answerType is set
        if (doubt.getAnswerType() == null || doubt.getAnswerType().isBlank()) {
            doubt.setAnswerType("MENTOR");
        }

        logger.info("User role: {}", student.getRole());
        if (student.getRole() != UserRole.STUDENT) {
            logger.error("User found but with incorrect role. Expected: student, Found: {}", student.getRole());
            throw new RuntimeException("Invalid student email. Only registered students can post doubts.");
        }

        // Find next available ID
        Query idQuery = entityManager.createNativeQuery(
                "SELECT COALESCE(MIN(t1.id + 1), 1) " +
                        "FROM doubts t1 " +
                        "LEFT JOIN doubts t2 ON t1.id + 1 = t2.id " +
                        "WHERE t2.id IS NULL");

        Long nextId = ((Number) idQuery.getSingleResult()).longValue();
        logger.info("Generated next ID: {}", nextId);

        doubt.setId(nextId);
        doubt.setStatus("pending");
        doubt.setPostedAt(new Date());

        // Generate AI answer if requested
        if ("AI".equalsIgnoreCase(doubt.getAnswerType())) {
            try {
                String aiAnswer = geminiService.generateAnswer(doubt.getDescription());
                if (aiAnswer != null && !aiAnswer.trim().isEmpty()) {
                    String htmlAnswer = MarkdownUtil.toHtml(aiAnswer);
                    doubt.setAnswer(htmlAnswer);
                    doubt.setStatus("answered");
                    doubt.setMentorEmail("ai@doubtapp.com");
                }
            } catch (Exception e) {
                logger.error("Failed to generate AI answer: {}", e.getMessage());
            }
        }

        // Insert using native SQL
        Query insertQuery = entityManager.createNativeQuery(
                "INSERT INTO doubts (id, title, description, status, student_email, mentor_email, answer, posted_at, answer_type) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

        insertQuery.setParameter(1, doubt.getId());
        insertQuery.setParameter(2, doubt.getTitle());
        insertQuery.setParameter(3, doubt.getDescription());
        insertQuery.setParameter(4, doubt.getStatus());
        insertQuery.setParameter(5, doubt.getStudentEmail());
        insertQuery.setParameter(6, doubt.getMentorEmail());
        insertQuery.setParameter(7, doubt.getAnswer());
        insertQuery.setParameter(8, doubt.getPostedAt());
        insertQuery.setParameter(9, doubt.getAnswerType());

        insertQuery.executeUpdate();
        logger.info("Successfully saved doubt with ID: {}", doubt.getId());
        return doubt;
    }

    // ✅ Admin or Mentor can view all doubts
    public List<Doubt> getAllDoubts() {
        return doubtRepository.findAll();
    }

    // ✅ Get all doubts posted by a particular student
    public List<Doubt> getDoubtsByStudent(String email) {
        // Validate student email
        User student = userRepository.findByEmail(email);
        if (student == null || student.getRole() != UserRole.STUDENT) {
            throw new RuntimeException("Invalid student email");
        }
        return doubtRepository.findByStudentEmail(email);
    }

    // ✅ Get all doubts assigned to or answered by a mentor
    public List<Doubt> getDoubtsByMentor(String email) {
        // Validate mentor email
        User mentor = userRepository.findByEmail(email);
        if (mentor == null || mentor.getRole() != UserRole.MENTOR) {
            throw new RuntimeException("Invalid mentor email");
        }
        return doubtRepository.findByMentorEmail(email);
    }

    // Get doubt by status
    public List<Doubt> getDoubtsByStatus(String status) {
        return doubtRepository.findByStatus(status);
    }

    // ✅ Mentor answers a doubt
    public Doubt answerDoubt(Long id, String answer, String mentorEmail) {
        // Validate mentor email
        User mentor = userRepository.findByEmail(mentorEmail);
        if (mentor == null || mentor.getRole() != UserRole.MENTOR) {
            throw new RuntimeException("Only registered mentors can answer doubts");
        }

        Doubt doubt = doubtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doubt not found"));

        // Allow any mentor to answer if unassigned or assigned to them
        if (doubt.getMentorEmail() == null || doubt.getMentorEmail().isBlank() || doubt.getMentorEmail().equals(mentorEmail)) {
            doubt.setAnswer(answer);
            doubt.setStatus("answered");
            doubt.setMentorEmail(mentorEmail);
            return doubtRepository.save(doubt);
        } else {
            throw new RuntimeException("This doubt is assigned to another mentor");
        }
    }

    public Doubt updateDoubtByStudent(Long id, StudentUpdateRequest request) {
        Doubt doubt = doubtRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Doubt not found"));

        if (!doubt.getStudentEmail().equals(request.getStudentEmail())) {
            throw new RuntimeException("Student can only update their own doubts");
        }

        if (!"pending".equals(doubt.getStatus())) {
            throw new RuntimeException("Cannot update doubt as it has already been answered");
        }

        doubt.setTitle(request.getTitle());
        doubt.setDescription(request.getDescription());
        return doubtRepository.save(doubt);
    }

    public Doubt updateDoubtByMentor(Long id, MentorUpdateRequest request) {
        Doubt doubt = doubtRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Doubt not found"));

        if (!doubt.getMentorEmail().equals(request.getMentorEmail())) {
            throw new RuntimeException("Mentor can only update doubts assigned to them");
        }

        doubt.setAnswer(request.getAnswer());
        doubt.setStatus("answered");
        return doubtRepository.save(doubt);
    }

    public void deleteDoubtByStudent(Long id, String studentEmail) {
        Doubt doubt = doubtRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Doubt not found"));

        if (!doubt.getStudentEmail().equals(studentEmail)) {
            throw new RuntimeException("Student can only delete their own doubts");
        }

        if (!"pending".equals(doubt.getStatus())) {
            throw new RuntimeException("Cannot delete doubt as it has already been answered");
        }

        doubtRepository.delete(doubt);
    }

    public Doubt deleteAnswerByMentor(Long id, String mentorEmail) {
        Doubt doubt = doubtRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Doubt not found"));

        if (doubt.getMentorEmail() == null || !doubt.getMentorEmail().equals(mentorEmail)) {
            throw new RuntimeException("Mentor can only delete answers from doubts assigned to them");
        }

        if (doubt.getAnswer() == null || doubt.getAnswer().trim().isEmpty()) {
            throw new RuntimeException("No answer exists to delete");
        }

        doubt.setAnswer(null);
        doubt.setStatus("pending");
        return doubtRepository.save(doubt);
    }

    // Utility for Markdown to HTML conversion
    private static class MarkdownUtil {
        private static final Parser parser = Parser.builder().build();
        private static final HtmlRenderer renderer = HtmlRenderer.builder().build();
        public static String toHtml(String markdown) {
            Node document = parser.parse(markdown);
            return renderer.render(document);
        }
    }
}

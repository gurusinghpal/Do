package com.doubtapp.backend.controller;

import com.doubtapp.backend.model.Doubt;
import com.doubtapp.backend.model.User;
import com.doubtapp.backend.model.UserRole;
import com.doubtapp.backend.repository.UserRepository;
import com.doubtapp.backend.service.DoubtService;
import com.doubtapp.backend.dto.StudentUpdateRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {
    private final UserRepository userRepository;
    private final DoubtService doubtService;

    public StudentController(UserRepository userRepository, DoubtService doubtService) {
        this.userRepository = userRepository;
        this.doubtService = doubtService;
    }

    @GetMapping("/dashboard")
    public String studentDashboard(Authentication authentication, Model model) {
        String studentEmail = authentication.getName();
        model.addAttribute("doubts", doubtService.getDoubtsByStudent(studentEmail));
        return "student/dashboard";
    }

    @GetMapping("/ask-doubt")
    public String askDoubtForm(Model model) {
        List<User> mentors = userRepository.findByRole(UserRole.MENTOR);
        model.addAttribute("mentors", mentors);
        return "student/ask-doubt";
    }

    @PostMapping("/ask-doubt")
    public String submitDoubt(@RequestParam String title,
                              @RequestParam String description,
                              @RequestParam String answerSource,
                              @RequestParam(required = false) String mentorEmail,
                              Authentication authentication) {
        String studentEmail = authentication.getName();
        Doubt doubt = new Doubt();
        doubt.setTitle(title);
        doubt.setDescription(description);
        doubt.setStudentEmail(studentEmail);
        doubt.setAnswerType(answerSource);
        if (answerSource.equals("MENTOR")) {
            doubt.setMentorEmail((mentorEmail != null && !mentorEmail.isBlank()) ? mentorEmail : null);
        } else {
            doubt.setMentorEmail(null);
        }
        doubtService.postDoubt(doubt);
        return "redirect:/student/dashboard";
    }

    @GetMapping("/doubt/{id}")
    public String viewDoubt(@PathVariable Long id, Model model, Authentication authentication) {
        Doubt doubt = doubtService.getDoubtsByStudent(authentication.getName())
            .stream().filter(d -> d.getId().equals(id)).findFirst().orElse(null);
        if (doubt == null) {
            return "redirect:/student/dashboard";
        }
        model.addAttribute("doubt", doubt);
        return "student/doubt-details";
    }

    @PostMapping("/doubt/{id}/request-mentor")
    public String requestMentorAnswer(@PathVariable Long id, Authentication authentication) {
        // Only allow if the doubt belongs to the student
        Doubt doubt = doubtService.getDoubtsByStudent(authentication.getName())
            .stream().filter(d -> d.getId().equals(id)).findFirst().orElse(null);
        if (doubt != null && "AI".equals(doubt.getAnswerType())) {
            doubt.setStatus("pending");
            doubt.setAnswerType("MENTOR");
            doubt.setMentorEmail(null);
            // Do NOT clear the answer field, keep the AI answer for mentor context
            StudentUpdateRequest req = new StudentUpdateRequest();
            req.setTitle(doubt.getTitle());
            req.setDescription(doubt.getDescription());
            req.setStudentEmail(doubt.getStudentEmail());
            doubtService.updateDoubtByStudent(doubt.getId(), req);
        }
        return "redirect:/student/doubt/" + id + "?mentorRequested";
    }

    @GetMapping("/doubt/{id}/edit")
    public String editDoubtForm(@PathVariable Long id, Model model, Authentication authentication) {
        Doubt doubt = doubtService.getDoubtsByStudent(authentication.getName())
            .stream().filter(d -> d.getId().equals(id)).findFirst().orElse(null);
        if (doubt == null || !"pending".equals(doubt.getStatus())) {
            return "redirect:/student/dashboard";
        }
        model.addAttribute("doubt", doubt);
        return "student/edit-doubt";
    }

    @PostMapping("/doubt/{id}/edit")
    public String editDoubtSubmit(@PathVariable Long id,
                                  @RequestParam String title,
                                  @RequestParam String description,
                                  Authentication authentication) {
        Doubt doubt = doubtService.getDoubtsByStudent(authentication.getName())
            .stream().filter(d -> d.getId().equals(id)).findFirst().orElse(null);
        if (doubt == null || !"pending".equals(doubt.getStatus())) {
            return "redirect:/student/dashboard";
        }
        StudentUpdateRequest req = new StudentUpdateRequest();
        req.setTitle(title);
        req.setDescription(description);
        req.setStudentEmail(doubt.getStudentEmail());
        doubtService.updateDoubtByStudent(id, req);
        return "redirect:/student/doubt/" + id + "?edited";
    }

    @PostMapping("/doubt/{id}/delete")
    public String deleteDoubt(@PathVariable Long id, Authentication authentication) {
        Doubt doubt = doubtService.getDoubtsByStudent(authentication.getName())
            .stream().filter(d -> d.getId().equals(id)).findFirst().orElse(null);
        if (doubt != null && "pending".equals(doubt.getStatus())) {
            doubtService.deleteDoubtByStudent(id, doubt.getStudentEmail());
        }
        return "redirect:/student/dashboard";
    }
} 
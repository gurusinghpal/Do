package com.doubtapp.backend.controller;

import com.doubtapp.backend.model.Doubt;
import com.doubtapp.backend.repository.DoubtRepository;
import com.doubtapp.backend.service.DoubtService;
import com.doubtapp.backend.repository.UserRepository;
import com.doubtapp.backend.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/mentor")
public class MentorController {
    private final DoubtRepository doubtRepository;
    private final DoubtService doubtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MentorController(DoubtRepository doubtRepository, DoubtService doubtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.doubtRepository = doubtRepository;
        this.doubtService = doubtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String mentorDashboard(Authentication authentication, Model model) {
        String mentorEmail = authentication.getName();
        List<Doubt> doubts = doubtRepository.findByMentorEmailOrMentorEmailIsNull(mentorEmail);
        model.addAttribute("doubts", doubts);
        User mentor = userRepository.findByEmail(mentorEmail);
        model.addAttribute("mentorName", mentor != null ? mentor.getName() : "Mentor");
        return "mentor/dashboard";
    }

    @GetMapping("/doubt/{id}")
    public String viewDoubt(@PathVariable Long id, Model model) {
        Optional<Doubt> doubtOpt = doubtRepository.findById(id);
        if (doubtOpt.isEmpty()) {
            return "redirect:/mentor/dashboard";
        }
        model.addAttribute("doubt", doubtOpt.get());
        return "mentor/doubt-details";
    }

    @PostMapping("/doubt/{id}/answer")
    public String answerDoubt(@PathVariable Long id,
                              @RequestParam String answer,
                              Authentication authentication) {
        String mentorEmail = authentication.getName();
        doubtService.answerDoubt(id, answer, mentorEmail);
        return "redirect:/mentor/doubt/" + id + "?answered";
    }

    @GetMapping("/doubt/{id}/edit")
    public String editAnswerForm(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<Doubt> doubtOpt = doubtRepository.findById(id);
        if (doubtOpt.isEmpty()) {
            return "redirect:/mentor/dashboard";
        }
        Doubt doubt = doubtOpt.get();
        if (!authentication.getName().equals(doubt.getMentorEmail()) || !"answered".equals(doubt.getStatus())) {
            return "redirect:/mentor/dashboard";
        }
        model.addAttribute("doubt", doubt);
        return "mentor/edit-answer";
    }

    @PostMapping("/doubt/{id}/edit")
    public String editAnswerSubmit(@PathVariable Long id,
                                   @RequestParam String answer,
                                   Authentication authentication) {
        Optional<Doubt> doubtOpt = doubtRepository.findById(id);
        if (doubtOpt.isEmpty()) {
            return "redirect:/mentor/dashboard";
        }
        Doubt doubt = doubtOpt.get();
        if (!authentication.getName().equals(doubt.getMentorEmail()) || !"answered".equals(doubt.getStatus())) {
            return "redirect:/mentor/dashboard";
        }
        doubt.setAnswer(answer);
        doubtRepository.save(doubt);
        return "redirect:/mentor/doubt/" + id + "?edited";
    }

    @PostMapping("/doubt/{id}/delete")
    public String deleteAnswer(@PathVariable Long id, Authentication authentication) {
        Optional<Doubt> doubtOpt = doubtRepository.findById(id);
        if (doubtOpt.isPresent()) {
            Doubt doubt = doubtOpt.get();
            if (authentication.getName().equals(doubt.getMentorEmail()) && "answered".equals(doubt.getStatus())) {
                doubt.setAnswer(null);
                doubt.setStatus("pending");
                doubtRepository.save(doubt);
            }
        }
        return "redirect:/mentor/dashboard";
    }

    @GetMapping("/profile")
    public String mentorProfile(Authentication authentication, Model model) {
        String mentorEmail = authentication.getName();
        User mentor = userRepository.findByEmail(mentorEmail);
        model.addAttribute("mentor", mentor);
        // Stats
        List<Doubt> allDoubts = doubtRepository.findByMentorEmailOrMentorEmailIsNull(mentorEmail);
        long total = allDoubts.size();
        long answered = allDoubts.stream().filter(d -> "answered".equalsIgnoreCase(d.getStatus())).count();
        long pending = allDoubts.stream().filter(d -> "pending".equalsIgnoreCase(d.getStatus())).count();
        model.addAttribute("totalDoubts", total);
        model.addAttribute("answeredDoubts", answered);
        model.addAttribute("pendingDoubts", pending);
        // Recent doubts (last 3)
        allDoubts.sort((a, b) -> b.getId().compareTo(a.getId()));
        model.addAttribute("recentDoubts", allDoubts.stream().limit(3).toList());
        return "mentor/profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(
        @RequestParam String name,
        @RequestParam(required = false) String password,
        Authentication authentication
    ) {
        String email = authentication.getName();
        User mentor = userRepository.findByEmail(email);
        if (mentor != null) {
            mentor.setName(name);
            if (password != null && !password.isBlank()) {
                mentor.setPassword(passwordEncoder.encode(password));
            }
            userRepository.save(mentor);
        }
        return "redirect:/mentor/profile?edited";
    }
} 
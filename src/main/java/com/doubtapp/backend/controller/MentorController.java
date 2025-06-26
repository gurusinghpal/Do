package com.doubtapp.backend.controller;

import com.doubtapp.backend.model.Doubt;
import com.doubtapp.backend.repository.DoubtRepository;
import com.doubtapp.backend.service.DoubtService;
import org.springframework.security.core.Authentication;
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

    public MentorController(DoubtRepository doubtRepository, DoubtService doubtService) {
        this.doubtRepository = doubtRepository;
        this.doubtService = doubtService;
    }

    @GetMapping("/dashboard")
    public String mentorDashboard(Authentication authentication, Model model) {
        String mentorEmail = authentication.getName();
        List<Doubt> doubts = doubtRepository.findByMentorEmailOrMentorEmailIsNull(mentorEmail);
        model.addAttribute("doubts", doubts);
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
} 
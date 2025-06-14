package com.doubtapp.backend.controller;

import com.doubtapp.backend.dto.AnswerRequest;
import com.doubtapp.backend.model.Doubt;
import com.doubtapp.backend.service.DoubtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/doubts")
@CrossOrigin(origins = "*")
public class DoubtController {

    @Autowired
    private DoubtService doubtService;

    @PostMapping("/post")
    public ResponseEntity<?> postDoubt(@RequestBody Doubt doubt) {
        try {
            return ResponseEntity.ok(doubtService.postDoubt(doubt));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/answer/{id}")
    public ResponseEntity<?> answerDoubt(
            @PathVariable Long id,
            @RequestBody AnswerRequest request) {
        try {
            return ResponseEntity.ok(doubtService.answerDoubt(id, request.getAnswer(), request.getMentorEmail()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Doubt>> getAll() {
        return ResponseEntity.ok(doubtService.getAllDoubts());
    }

    @GetMapping("/student")
    public ResponseEntity<?> getByStudent(@RequestParam String email) {
        try {
            return ResponseEntity.ok(doubtService.getDoubtsByStudent(email));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<List<Doubt>> getByStatus(@RequestParam String status) {
        return ResponseEntity.ok(doubtService.getDoubtsByStatus(status));
    }

    @GetMapping("/mentor")
    public ResponseEntity<?> getByMentor(@RequestParam String email) {
        try {
            return ResponseEntity.ok(doubtService.getDoubtsByMentor(email));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Simple error response class
    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}

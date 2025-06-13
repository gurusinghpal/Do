package com.doubtapp.backend.controller;

import com.doubtapp.backend.dto.AnswerRequest;
import com.doubtapp.backend.model.Doubt;
import com.doubtapp.backend.service.DoubtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doubts")
@CrossOrigin(origins = "*")
public class DoubtController {

    @Autowired
    private DoubtService doubtService;

    @PostMapping("/post")
    public ResponseEntity<Doubt> postDoubt(@RequestBody Doubt doubt) {
        return ResponseEntity.ok(doubtService.postDoubt(doubt));
    }

    @PostMapping("/answer/{id}")
    public ResponseEntity<Doubt> answerDoubt(
            @PathVariable Long id,
            @RequestBody AnswerRequest request) {
        return ResponseEntity.ok(doubtService.answerDoubt(id, request.getAnswer(), request.getMentorEmail()));
    }


    @GetMapping("/all")
    public ResponseEntity<List<Doubt>> getAll() {
        return ResponseEntity.ok(doubtService.getAllDoubts());
    }

    @GetMapping("/student")
    public ResponseEntity<List<Doubt>> getByStudent(@RequestParam String email) {
        return ResponseEntity.ok(doubtService.getDoubtsByStudent(email));
    }

    @GetMapping("/status")
    public ResponseEntity<List<Doubt>> getByStatus(@RequestParam String status) {
        return ResponseEntity.ok(doubtService.getDoubtsByStatus(status));
    }


    @GetMapping("/mentor")
    public ResponseEntity<List<Doubt>> getByMentor(@RequestParam String email) {
        return ResponseEntity.ok(doubtService.getDoubtsByMentor(email));
    }
}

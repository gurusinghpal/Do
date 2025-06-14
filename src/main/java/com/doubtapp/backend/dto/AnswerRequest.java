package com.doubtapp.backend.dto;

import lombok.Data;

@Data
public class AnswerRequest {
    private String answer;
    private String mentorEmail;
}

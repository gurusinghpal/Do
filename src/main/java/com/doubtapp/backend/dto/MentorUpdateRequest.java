package com.doubtapp.backend.dto;

import lombok.Data;

@Data
public class MentorUpdateRequest {
    private String answer;
    private String status;
    private String mentorEmail;
} 
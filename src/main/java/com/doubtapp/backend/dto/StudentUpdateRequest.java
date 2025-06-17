package com.doubtapp.backend.dto;

import lombok.Data;

@Data
public class StudentUpdateRequest {
    private String title;
    private String description;
    private String studentEmail;
} 
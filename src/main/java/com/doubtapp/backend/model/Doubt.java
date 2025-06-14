package com.doubtapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "doubts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doubt {
    @Id
    private Long id;

    private String title;
    private String description;
    private String status;
    private String studentEmail;
    private String mentorEmail;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Temporal(TemporalType.TIMESTAMP)
    private Date postedAt;

    @Column(name = "answer_type")
    private String answerType;

    public Doubt(String title, String description, String studentEmail) {
        this.title = title;
        this.description = description;
        this.studentEmail = studentEmail;
        this.status = "pending";
        this.postedAt = new Date();
        this.answerType = "MENTOR";
    }

    @PrePersist
    public void prePersist() {
        if (this.answerType == null || this.answerType.isBlank()) {
            this.answerType = "MENTOR";
        }
        if (this.status == null || this.status.isBlank()) {
            this.status = "pending";
        }
        if (this.postedAt == null) {
            this.postedAt = new Date();
        }
    }

    public void setAnswerType(String answerType) {
        this.answerType = (answerType == null || answerType.isBlank()) ? "MENTOR" : answerType;
    }
}


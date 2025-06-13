package com.doubtapp.backend.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "doubts")
public class Doubt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private String status; // e.g., "pending", "answered"

    private String studentEmail;
    private String mentorEmail;

    @Column(length = 1000)
    private String answer;

    @Temporal(TemporalType.TIMESTAMP)
    private Date postedAt;

    public Doubt() {
        this.postedAt = new Date();
        this.status = "pending"; // Default status
    }

    // Constructor with fields (excluding ID)
    public Doubt(String title, String description, String studentEmail) {
        this.title = title;
        this.description = description;
        this.studentEmail = studentEmail;
        this.status = "pending";
        this.postedAt = new Date();
    }

    // ------------------- Getters and Setters -------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getMentorEmail() {
        return mentorEmail;
    }

    public void setMentorEmail(String mentorEmail) {
        this.mentorEmail = mentorEmail;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(Date postedAt) {
        this.postedAt = postedAt;
    }
}


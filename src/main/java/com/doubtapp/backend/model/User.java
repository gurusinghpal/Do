package com.doubtapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String uid;
    private String name;
    private String email;
    private String role; // "user" or "mentor"
}

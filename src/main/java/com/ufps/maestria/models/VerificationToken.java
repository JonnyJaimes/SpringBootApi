package com.bezkoder.springjwt.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private LocalDateTime expiryDate;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    // Constructors, getters, and setters
}

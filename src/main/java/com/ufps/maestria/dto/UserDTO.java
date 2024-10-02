package com.bezkoder.springjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private boolean enabled;
    private String verificationToken;
    private LocalDateTime tokenExpiryTime;
    private Set<String> roles; // Include roles

    // Constructors, getters, and setters
    public UserDTO() {}

    public UserDTO(Long id, String username, String email, boolean enabled, String verificationToken, LocalDateTime tokenExpiryTime) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.verificationToken = verificationToken;
        this.tokenExpiryTime = tokenExpiryTime;
    }

    // Getters and Setters...
}

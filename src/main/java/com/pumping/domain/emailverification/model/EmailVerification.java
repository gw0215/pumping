package com.pumping.domain.emailverification.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String code;

    private LocalDateTime expiresAt;

    private boolean verified = false;

    public EmailVerification(String email, String code, LocalDateTime expiresAt) {
        this.email = email;
        this.code = code;
        this.expiresAt = expiresAt;
    }

    public void updateCode(String code) {
        this.code = code;
    }

    public void updateExpiredAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}

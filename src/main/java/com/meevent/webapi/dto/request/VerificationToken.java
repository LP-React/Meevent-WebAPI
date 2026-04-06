package com.meevent.webapi.dto.request;

import java.time.LocalDateTime;

import com.meevent.webapi.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "verification_tokens")
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User toUser;

    private LocalDateTime expiryDate;
    
    private boolean used = false;

    public VerificationToken(String token, User user, int expiryHours) {
        this.token = token;
        this.toUser = user;
        this.expiryDate = LocalDateTime.now().plusHours(expiryHours); /*<---  */
        this.used = false;
    }
}
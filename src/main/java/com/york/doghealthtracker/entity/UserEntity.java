package com.york.doghealthtracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "personal_data")
@Getter
@Setter
public class UserEntity {

    @Id
    @Column(name = "pd_participant_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "pd_email")
    private String email;

    @Column(name = "pd_password")
    private String password;

    @Column(name = "pd_role")
    private String role = "USER";

    @Column(name = "pd_creation_ts")
    private Instant creationTimestamp;

    @Column(name = "pd_reset_token")
    private String resetToken;

    @Column(name = "pd_reset_token_expiration")
    private Instant resetTokenExpiration;

    @PrePersist
    protected void onCreate() {
        if (creationTimestamp == null) {
            creationTimestamp = Instant.now();
        }
    }

}

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

    @Column(name = "pd_email", unique = true, nullable = false)
    private String email;

    @Column(name = "pd_password", nullable = false)
    private String password;

    @Column(name = "pd_role", nullable = false)
    private String role = "USER";

    @Column(name = "pd_creation_ts", updatable = false)
    private Instant creationTimestamp;

    @PrePersist
    protected void onCreate() {
        if (creationTimestamp == null) {
            creationTimestamp = Instant.now();
        }
    }

}

package com.york.doghealthtracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.net.URI;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_record")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HealthRecordEntity {

    @Id
    @Column(name = "hr_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hr_dog_id")
    private DogEntity dog;

    @Column(name = "hr_document_name")
    private String documentName;

    @Column(name = "hr_document_url")
    private String documentUrl;

    @Column(name = "hr_content_type")
    private String contentType;

    @Column(name = "hr_created_ts")
    private LocalDateTime createdTs;
}

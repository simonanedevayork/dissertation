package com.york.doghealthtracker.entity;

import com.york.doghealthtracker.model.GingivitisStatus;
import com.york.doghealthtracker.model.PlaqueStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dental")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DentalEntity {

    @Id
    @Column(name = "dl_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dl_dog_id")
    private DogEntity dog;

    @Enumerated(EnumType.STRING)
    @Column(name = "dl_plaque_status")
    private PlaqueStatus plaqueStatus;

    @Column(name = "dl_tooth_loss")
    private Boolean toothLoss;

    @Enumerated(EnumType.STRING)
    @Column(name = "dl_gingivitis_status")
    private GingivitisStatus gingivitisStatus;

    @Column(name = "dl_last_cleaning_date")
    private LocalDate lastCleaningDate;

    @Column(name = "dl_created_ts")
    private LocalDateTime createdTs;
}
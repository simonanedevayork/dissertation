package com.york.doghealthtracker.entity;

import com.york.doghealthtracker.model.MurmurStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "heart")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeartEntity {

    @Id
    @Column(name = "ht_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ht_dog_id")
    private DogEntity dog;

    @Column(name = "ht_fatigue")
    private Boolean hasFatigue;

    @Column(name = "ht_coughing")
    private Boolean isCoughing;

    @Enumerated(EnumType.STRING)
    @Column(name = "ht_murmurs")
    private MurmurStatus murmursStatus;

    @Column(name = "ht_heart_rate")
    private Integer heartRate;

    @Column(name = "ht_breathing_rate")
    private Integer breathingRate;

    @Column(name = "HT_DIROFILARIA_LAST_PREVENTION")
    private LocalDate lastDirofilariaLastPrevention;

    @Column(name = "ht_created_ts")
    private LocalDateTime createdTs;
}

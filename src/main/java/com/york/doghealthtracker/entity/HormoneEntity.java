package com.york.doghealthtracker.entity;

import com.york.doghealthtracker.model.HormoneCategory;
import com.york.doghealthtracker.model.HormoneStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "hormone")
@Getter
@Setter
public class HormoneEntity {

    @Id
    @Column(name = "hm_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hm_dog_id", nullable = false)
    private DogEntity dog;

    @Enumerated(EnumType.STRING)
    @Column(name = "hm_type", nullable = false)
    private HormoneCategory type;

    @Enumerated(EnumType.STRING)
    @Column(name = "hm_status", nullable = false)
    private HormoneStatus status;


    @Column(name = "hm_created_ts", nullable = false)
    private LocalDateTime createdTs;
}
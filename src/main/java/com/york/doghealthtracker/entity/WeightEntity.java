package com.york.doghealthtracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "weight")
@Getter
@Setter
public class WeightEntity {

    @Id
    @Column(name = "wg_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WG_DOG_ID")
    private DogEntity dog;

    @Column(name = "WG_CURRENT")
    private Float current;

    @Column(name = "WG_CREATED_TS")
    private LocalDateTime createdTs;

}

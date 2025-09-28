package com.york.doghealthtracker.entity;

import com.york.doghealthtracker.model.HormoneCategory;
import com.york.doghealthtracker.model.MobilityCategory;
import com.york.doghealthtracker.model.QuizCategoryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "mobility")
@Getter
@Setter
public class MobilityEntity {

    @Id
    @Column(name = "mb_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mb_dog_id", nullable = false)
    private DogEntity dog;

    @Enumerated(EnumType.STRING)
    @Column(name = "mb_type", nullable = false)
    private MobilityCategory type;

    @Enumerated(EnumType.STRING)
    @Column(name = "mb_status", nullable = false)
    private QuizCategoryStatus status;

    @Column(name = "mb_created_ts", nullable = false)
    private LocalDateTime createdTs;

}
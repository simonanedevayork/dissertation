package com.york.doghealthtracker.entity;

import com.york.doghealthtracker.model.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "dog")
@Getter
@Setter
public class DogEntity {

    @Id
    @Column(name = "dog_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "dog_owner")
    private UserEntity owner;

    @Column(name = "dog_name")
    private String name;

    @Column(name = "dog_breed")
    private String breed;

    @Column(name = "dog_gender")
    private Gender gender;

    @Column(name = "dog_birth_date")
    private LocalDate birthDate;

    @Column(name = "dog_photo")
    private String photo;

    @Column(name = "dog_nurtured")
    private Boolean isNeutered;
}

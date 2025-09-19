package com.york.doghealthtracker.entity;

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

    @Column(name = "DOG_NAME")
    private String name;

    @Column(name = "DOG_BREED")
    private String breed;

    @Column(name = "DOG_GENDER")
    private String gender;

    @Column(name = "DOG_BIRTH_DATE")
    private LocalDate birthDate;

    @Column(name = "DOG_PHOTO")
    private String photo;

    @Column(name = "DOG_NURTURED")
    private Boolean isNeutered;
}

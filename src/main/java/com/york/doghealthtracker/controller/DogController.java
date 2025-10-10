package com.york.doghealthtracker.controller;

import com.york.doghealthtracker.api.DogsApi;
import com.york.doghealthtracker.model.DogResponse;
import com.york.doghealthtracker.model.Gender;
import com.york.doghealthtracker.service.DogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
public class DogController implements DogsApi {

    private final DogService dogService;

    public DogController(DogService dogService) {
        this.dogService = dogService;
    }

    @Override
    public ResponseEntity<DogResponse> addDog(String name, Gender gender, String breed, LocalDate birthDate, Boolean isNeutered, MultipartFile file) {
        try {
            DogResponse created = dogService.createDog(name, gender, breed, birthDate, isNeutered, file);
            return ResponseEntity.status(201).body(created);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public ResponseEntity<DogResponse> getDogById(String dogId) {
        return dogService.getDogById(dogId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<DogResponse> updateDogById(String dogId, String name, Gender gender, String breed, LocalDate birthDate, Boolean isNeutered, MultipartFile file) {
        try {
            return dogService.updateDog(dogId, name, gender, breed, birthDate, isNeutered, file)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

}
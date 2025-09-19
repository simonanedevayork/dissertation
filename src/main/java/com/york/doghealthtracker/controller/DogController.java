package com.york.doghealthtracker.controller;

import com.york.doghealthtracker.api.DogsApi;
import com.york.doghealthtracker.model.DogRequest;
import com.york.doghealthtracker.model.DogResponse;
import com.york.doghealthtracker.service.DogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DogController implements DogsApi {

    private final DogService dogService;

    public DogController(DogService dogService) {
        this.dogService = dogService;
    }

    @Override
    public ResponseEntity<DogResponse> addDog(DogRequest dogRequest) {
        DogResponse created = dogService.createDog(dogRequest);
        return ResponseEntity.status(201).body(created);
    }

    @Override
    public ResponseEntity<DogResponse> getDogById(String dogId) {
        return dogService.getDogById(dogId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<DogResponse> updateDogById(String dogId, DogRequest dogRequest) {
        return dogService.updateDog(dogId, dogRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
package com.york.doghealthtracker.controller;

import com.york.doghealthtracker.api.WeightApi;
import com.york.doghealthtracker.model.WeightRequest;
import com.york.doghealthtracker.model.WeightResponse;
import com.york.doghealthtracker.service.WeightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WeightController implements WeightApi {

    private final WeightService weightService;

    public WeightController(WeightService weightService) {
        this.weightService = weightService;
    }

    @Override
    public ResponseEntity<WeightResponse> addWeight(String dogId, WeightRequest weightRequest) {
        return weightService.addWeight(dogId, weightRequest)
                .map(saved -> ResponseEntity.status(201).body(saved))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<WeightResponse>> getWeights(String dogId) {
        List<WeightResponse> weights = weightService.getWeights(dogId);
        return ResponseEntity.ok(weights);
    }

    @Override
    public ResponseEntity<Void> deleteWeight(String dogId, String weightId) {
         weightService.deleteWeight(weightId);
        return ResponseEntity.noContent().build();
    }
}
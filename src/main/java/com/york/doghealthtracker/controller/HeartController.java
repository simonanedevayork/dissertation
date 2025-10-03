package com.york.doghealthtracker.controller;

import com.york.doghealthtracker.api.HeartApi;
import com.york.doghealthtracker.model.HeartRequest;
import com.york.doghealthtracker.model.HeartResponse;
import com.york.doghealthtracker.service.HeartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HeartController implements HeartApi {

    private final HeartService heartService;

    public HeartController(HeartService heartService) {
        this.heartService = heartService;
    }

    @Override
    public ResponseEntity<HeartResponse> addHeartRecord(String dogId, HeartRequest heartRequest) {
        return heartService.addHeartStatus(dogId, heartRequest)
                .map(saved -> ResponseEntity.status(201).body(saved))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<HeartResponse>> getHeartRecords(String dogId) {
        List<HeartResponse> heartStatuses = heartService.getHeartStatuses(dogId);
        return ResponseEntity.ok(heartStatuses);
    }

    @Override
    public ResponseEntity<Void> deleteHeartRecord(String dogId, String heartId) {
        boolean deleted = heartService.deleteHeartStatus(dogId, heartId);
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<HeartResponse> getHeartById(String dogId, String heartId) {
        return heartService.getHeartStatusById(dogId, heartId)
                .map(response -> ResponseEntity.status(200).body(response))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<HeartResponse> updateHeartRecord(String dogId, String heartId, HeartRequest heartRequest) {
        return heartService.updateHeartStatus(dogId, heartId, heartRequest)
                .map(saved -> ResponseEntity.status(201).body(saved))
                .orElse(ResponseEntity.notFound().build());
    }
}

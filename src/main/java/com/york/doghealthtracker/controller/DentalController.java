package com.york.doghealthtracker.controller;

import com.york.doghealthtracker.api.DentalApi;
import com.york.doghealthtracker.model.DentalRequest;
import com.york.doghealthtracker.model.DentalResponse;
import com.york.doghealthtracker.service.DentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DentalController implements DentalApi {

    private final DentalService dentalService;

    public DentalController(DentalService dentalService) {
        this.dentalService = dentalService;
    }

    @Override
    public ResponseEntity<DentalResponse> addDentalRecord(String dogId, DentalRequest dentalRequest) {
        return dentalService.addDentalStatus(dogId, dentalRequest)
                .map(saved -> ResponseEntity.status(201).body(saved))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<DentalResponse>> getDentalRecords(String dogId) {
        List<DentalResponse> dentalStatuses = dentalService.getDentalStatuses(dogId);
        return ResponseEntity.ok(dentalStatuses);
    }

    @Override
    public ResponseEntity<DentalResponse> getDentalById(String dogId, String dentalId) {
        return dentalService.getDentalStatusById(dogId, dentalId)
                .map(response -> ResponseEntity.status(200).body(response))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<DentalResponse> updateDental(String dogId, String dentalId, DentalRequest dentalRequest) {
        return dentalService.updateDentalStatus(dogId, dentalId, dentalRequest)
                .map(saved -> ResponseEntity.status(201).body(saved))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteDental(String dogId, String dentalId) {
        boolean deleted = dentalService.deleteDentalStatus(dogId, dentalId);
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

package com.york.doghealthtracker.controller;

import com.york.doghealthtracker.api.HealthRecordsApi;
import com.york.doghealthtracker.model.HealthRecordResponse;
import com.york.doghealthtracker.service.HealthRecordService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class HealthRecordController implements HealthRecordsApi {

    private final HealthRecordService healthRecordService;

    public HealthRecordController(HealthRecordService healthRecordService) {
        this.healthRecordService = healthRecordService;
    }

    @Override
    public ResponseEntity<HealthRecordResponse> addHealthRecord(String dogId, MultipartFile file, String documentName) {
        return ResponseEntity.status(201)
                .body(healthRecordService.addHealthRecord(dogId, file, documentName));
    }

    @Override
    public ResponseEntity<Resource> downloadHealthRecord(String dogId, String healthRecordId) {
        return healthRecordService.getHealthRecordFile(dogId, healthRecordId);
    }

    @Override
    public ResponseEntity<Void> deleteHealthRecord(String dogId, String healthRecordId) {
        healthRecordService.deleteHealthRecord(dogId, healthRecordId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<HealthRecordResponse> getHealthRecordById(String dogId, String healthRecordId) {
        return null;
    }

    @Override
    public ResponseEntity<List<HealthRecordResponse>> getHealthRecords(String dogId) {
        return ResponseEntity.ok(healthRecordService.getHealthRecords(dogId));
    }

//    @Override
//    public ResponseEntity<HealthRecordResponse> updateHealthRecord(String dogId, String healthRecordId, MultipartFile file, String documentName) {
//
//    }

}

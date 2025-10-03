package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.entity.HealthRecordEntity;
import com.york.doghealthtracker.exception.ResourceNotFoundException;
import com.york.doghealthtracker.exception.FileStorageException;
import com.york.doghealthtracker.model.HealthRecordResponse;
import com.york.doghealthtracker.repository.DogRepository;
import com.york.doghealthtracker.repository.HealthRecordRepository;
import com.york.doghealthtracker.service.security.UserContextService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Service responsible for uploading and downloading file records.
 *
 * @PreAuthorize method annotations validate that user has authorization to access the given resource.
 */
@Service
@Slf4j
public class HealthRecordService {
    private final HealthRecordRepository healthRecordRepository;
    private final DogRepository dogRepository;
    private final FileStorageService fileStorageService;

    @Value("${app.base-url}")
    private String baseUrl;

    public HealthRecordService(HealthRecordRepository healthRecordRepository, DogRepository dogRepository, FileStorageService fileStorageService) {
        this.healthRecordRepository = healthRecordRepository;
        this.dogRepository = dogRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Processes and stores a health record file into the file system.
     *
     * @param dogId        The dog id to save health record for.
     * @param file         The file to save.
     * @param documentName The given name of the file to save.
     * @return HealthRecordResponse with the health record data.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId)")
    public HealthRecordResponse addHealthRecord(String dogId, MultipartFile file, String documentName) {
        DogEntity dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new RuntimeException("Dog not found: " + dogId));

        try {
            String storedFilename = fileStorageService.store(dogId, file);
            String fileUrl = String.format("%s/uploads/%s/%s", baseUrl, dogId, storedFilename);

            HealthRecordEntity entity = HealthRecordEntity.builder()
                    .dog(dog)
                    .documentName(documentName)
                    .documentUrl(fileUrl)
                    .contentType(file.getContentType())
                    .createdTs(LocalDateTime.now((ZoneOffset.UTC)))
                    .build();

            HealthRecordEntity saved = healthRecordRepository.save(entity);

            return toHealthRecordResponse(saved);

        } catch (Exception e) {
            throw new FileStorageException("File upload failed", e);
        }
    }

    /**
     * Retrieves a downloadable resource version of a given health record.
     *
     * @param dogId          The dog id to obtain the health record for, used to load the file.
     * @param healthRecordId The health record id to obtain.
     * @return Resource file to download.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId) && @authorizationService.hasHealthRecordOwnership(#dogId, #healthRecordId)")
    public ResponseEntity<Resource> getHealthRecordFile(String dogId, String healthRecordId) {
        HealthRecordEntity record = healthRecordRepository.findById(healthRecordId)
                .orElseThrow(() -> new RuntimeException("Health record not found for dogId=" + dogId));

        try {
            String filename = Paths.get(record.getDocumentUrl()).getFileName().toString();
            Resource resource = fileStorageService.load(dogId, filename);

            if (!resource.exists()) {
                throw new RuntimeException("File not found: " + filename);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + record.getDocumentName() + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("Could not read file", e);
        }
    }

    /**
     * Retrieves a list of all health records for a given dog.
     *
     * @param dogId The dog id to retrieve a list of health records for.
     * @return A list of HealthRecordResponse containing all health records for the given dog.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId)")
    public List<HealthRecordResponse> getHealthRecords(String dogId) {
        return healthRecordRepository.findByDog_Id(dogId)
                .stream()
                .map(this::toHealthRecordResponse)
                .toList();
    }

    /**
     * Deletes a given health record, if exists.
     *
     * @param dogId          The dog id to delete the health record for.
     * @param healthRecordId The health record id to delete.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId) && @authorizationService.hasHealthRecordOwnership(#dogId, #healthRecordId)")
    @Transactional
    public void deleteHealthRecord(String dogId, String healthRecordId) {

        HealthRecordEntity record = healthRecordRepository.findById(healthRecordId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Health record with id %s not found", healthRecordId)));

        healthRecordRepository.delete(record);
        log.info("Health record with id: {} deleted for dog with id: {})", healthRecordId, dogId);
    }

    /**
     * Maps the HealthRecordEntity object to a HealthRecordResponse object containing the health record information.
     *
     * @param entity The HealthRecordEntity object to map to HealthRecordResponse.
     * @return HealthRecordResponse object.
     */
    private HealthRecordResponse toHealthRecordResponse(HealthRecordEntity entity) {
        HealthRecordResponse resp = new HealthRecordResponse();
        resp.setHealthRecordId(entity.getId());
        resp.setDogId(entity.getDog().getId());
        resp.setDocumentName(entity.getDocumentName());
        resp.setDocumentUrl(URI.create(entity.getDocumentUrl()));
        resp.setContentType(entity.getContentType());
        resp.setCreatedTs(OffsetDateTime.of(entity.getCreatedTs(), ZoneOffset.UTC));

        return resp;
    }

}
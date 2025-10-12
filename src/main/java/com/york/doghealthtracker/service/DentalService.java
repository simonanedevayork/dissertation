package com.york.doghealthtracker.service;

import com.york.doghealthtracker.config.HighlightConfig;
import com.york.doghealthtracker.entity.DentalEntity;
import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.model.*;
import com.york.doghealthtracker.repository.DentalRepository;
import com.york.doghealthtracker.repository.DogRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for dog dental status management.
 *
 * @PreAuthorize method annotations validate that user has authorization to access the given resource.
 */
@Service
@Log4j2
public class DentalService {

    private final DentalRepository dentalRepository;
    private final DogRepository dogRepository;
    private final HighlightConfig highlightConfig;

    public DentalService(DentalRepository dentalRepository, DogRepository dogRepository, HighlightConfig highlightConfig) {
        this.dentalRepository = dentalRepository;
        this.dogRepository = dogRepository;
        this.highlightConfig = highlightConfig;
    }

    /**
     * Adds a new dental status for a given dog.
     *
     * @param dogId   The dog to add a dental status for.
     * @param request The dental status request.
     * @return an Optional object of DentalResponse containing the saved object, or an empty Optional in case of an
     * error.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId)")
    public Optional<DentalResponse> addDentalStatus(String dogId, DentalRequest request) {
        DogEntity dog = dogRepository.findById(dogId).orElse(null);
        if (dog == null) {
            return Optional.empty();
        }

        DentalEntity entity = DentalEntity.builder()
                .dog(dog)
                .plaqueStatus(request.getPlaqueStatus())
                .toothLoss(request.getToothLoss())
                .gingivitisStatus(request.getGingivitisStatus())
                .lastCleaningDate(request.getLastCleaningDate())
                .createdTs(LocalDateTime.now())
                .build();

        DentalEntity saved = dentalRepository.save(entity);
        return Optional.of(mapToDentalResponse(saved));
    }

    /**
     * Retrieves a list of all dental statuses for a given dog.
     *
     * @param dogId The dog id to retrieve a list of dental statuses for.
     * @return a list of DentalResponse containing all dental statuses for the given dog.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId)")
    public DentalListResponse getDentalListResponse(String dogId) {

        List<DentalEntity> dentalEntityList = dentalRepository.findByDog_Id(dogId);

        return new DentalListResponse()
                .dentalRecords(dentalEntityList.stream()
                        .map(this::mapToDentalResponse)
                        .map(dentalResponse -> dentalResponse
                                .status(calculateDentalStatus(dentalResponse)))
                        .collect(Collectors.toList()))
                .healthHighlights(getDentalHealthHighlights(dentalEntityList));
    }

    private QuizCategoryStatus calculateDentalStatus(DentalResponse dentalResponse) {
        int totalMetrics = 0;
        int healthyCount = 0;

        if (dentalResponse.getPlaqueStatus() != null) {
            totalMetrics++;
            PlaqueStatus plaque = dentalResponse.getPlaqueStatus();
            if (plaque == PlaqueStatus.NORM || plaque == PlaqueStatus.LO) {
                healthyCount++;
            }
        }

        if (dentalResponse.getToothLoss() != null) {
            totalMetrics++;
            if (!dentalResponse.getToothLoss()) {
                healthyCount++;
            }
        }

        if (dentalResponse.getGingivitisStatus() != null) {
            totalMetrics++;
            GingivitisStatus gum = dentalResponse.getGingivitisStatus();
            if (gum == GingivitisStatus.NONE) {
                healthyCount++;
            } else if (gum == GingivitisStatus.MILD) {
                healthyCount += 0.5;
            }
        }

        if (dentalResponse.getLastCleaningDate() != null) {
            totalMetrics++;
            LocalDate lastCleaning = dentalResponse.getLastCleaningDate();
            LocalDate oneYearAgo = LocalDate.now().minusMonths(12);

            if (lastCleaning.isAfter(oneYearAgo)) {
                healthyCount++;
            }
        }

        if (totalMetrics == 0) {
            return QuizCategoryStatus.YELLOW;
        }

        float ratio = (float) healthyCount / totalMetrics;

        if (ratio >= 0.75f) {
            return QuizCategoryStatus.GREEN;
        } else if (ratio >= 0.40f) {
            return QuizCategoryStatus.YELLOW;
        } else {
            return QuizCategoryStatus.RED;
        }
    }

    private List<HealthHighlight> getDentalHealthHighlights(List<DentalEntity> dentalEntityList) {

        List<HealthHighlight> healthHighlights = new ArrayList<>();

        DentalEntity latestRecord = dentalEntityList.stream()
                .max(Comparator.comparing(DentalEntity::getCreatedTs))
                .orElse(null);

        if (dentalEntityList.stream()
                .anyMatch(DentalEntity::getToothLoss)) {
            healthHighlights.add(constructHealthHighlight("toothLoss"));
        }

        if (latestRecord != null) {
            if (latestRecord.getLastCleaningDate() != null &&
                    latestRecord.getLastCleaningDate().isBefore(LocalDate.now().minusMonths(12))) {
                healthHighlights.add(constructHealthHighlight("overdueCleaning"));
            }

            if (latestRecord.getPlaqueStatus() != null) {
                switch (latestRecord.getPlaqueStatus()) {
                    case HI, NORM -> healthHighlights.add(constructHealthHighlight("highPlaque"));
                }
            }
        }

        if (healthHighlights.isEmpty()) {
            healthHighlights.add(constructHealthHighlight("dentalDefault"));
        }

        return healthHighlights;
    }

    private HealthHighlight constructHealthHighlight(String highlightType) {
        return highlightConfig.getMap().get(highlightType);
    }

    /**
     * Retrieves a requested dental status for a given dog by id.
     *
     * @param dogId    The dog id to retrieve dental status for.
     * @param dentalId The id of the DentalEntity to retrieve.
     * @return an Optional object of DentalResponse containing the requested object, or an empty Optional in case if such
     * dental status id does not exist, or if it does not belong to the provided dogId.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId) && @authorizationService.hasDentalStatusOwnership(#dogId, #dentalId)")
    public Optional<DentalResponse> getDentalStatusById(String dogId, String dentalId) {
        return dentalRepository.findById(dentalId)
                .filter(e -> e.getDog() != null && dogId.equals(e.getDog().getId()))
                .map(this::mapToDentalResponse);
    }

    /**
     * Updates a given dental status for a given dog in the database.
     *
     * @param dogId    The dog id to update dental status for.
     * @param dentalId The dental id to update.
     * @param request  The DentalRequest containing the new values to assign to the DentalEntity.
     * @return an Optional object of the DentalResponse with updated values, or an empty Optional in case of an error.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId) && @authorizationService.hasDentalStatusOwnership(#dogId, #dentalId)")
    public Optional<DentalResponse> updateDentalStatus(String dogId, String dentalId, DentalRequest request) {
        return dentalRepository.findById(dentalId)
                .filter(e -> e.getDog() != null && dogId.equals(e.getDog().getId()))
                .map(entity -> {
                    entity.setPlaqueStatus(request.getPlaqueStatus());
                    entity.setToothLoss(request.getToothLoss());
                    entity.setGingivitisStatus(request.getGingivitisStatus());
                    entity.setLastCleaningDate(request.getLastCleaningDate());
                    entity.setCreatedTs(LocalDateTime.now());
                    DentalEntity saved = dentalRepository.save(entity);

                    log.info("Successfully updated dental status with id: {} for dog with id: {}", dentalId, dogId);
                    return mapToDentalResponse(saved);
                });
    }

    /**
     * Deletes a given dental status of a given dog, if exists.
     *
     * @param dogId    The dog id to delete dental status for.
     * @param dentalId The dental status to delete.
     * @return true if dental status was deleted successfully, false otherwise.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId) && @authorizationService.hasDentalStatusOwnership(#dogId, #dentalId)")
    public boolean deleteDentalStatus(String dogId, String dentalId) {
        return dentalRepository.findById(dentalId)
                .filter(e -> e.getDog() != null && dogId.equals(e.getDog().getId()))
                .map(e -> {
                    dentalRepository.delete(e);
                    log.info("Successfully deleted dental status with id: {} for dog with id: {}", dentalId, dogId);
                    return true;
                })
                .orElseGet(() -> {
                    log.warn("Unsuccessful deletion of dental status with id: {} for dog with id: {}", dentalId, dogId);
                    return false;
                });
    }

    /**
     * Maps the DentalEntity object to a DentalResponse object containing the dental status information.
     *
     * @param entity The DentalEntity object to map to DentalResponse.
     * @return DentalResponse object.
     */
    private DentalResponse mapToDentalResponse(DentalEntity entity) {
        DentalResponse resp = new DentalResponse();
        resp.setDentalId(entity.getId());
        resp.setDogId(entity.getDog() != null ? entity.getDog().getId() : null);
        resp.setPlaqueStatus(entity.getPlaqueStatus());
        resp.setToothLoss(entity.getToothLoss());
        resp.setGingivitisStatus(entity.getGingivitisStatus());
        resp.setLastCleaningDate(entity.getLastCleaningDate());
        resp.setCreatedTs(entity.getCreatedTs().atOffset(ZoneOffset.UTC));
        return resp;
    }
}
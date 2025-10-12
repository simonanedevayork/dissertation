package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.entity.HeartEntity;
import com.york.doghealthtracker.model.*;
import com.york.doghealthtracker.repository.DogRepository;
import com.york.doghealthtracker.repository.HeartRepository;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service responsible for dog heart status management.
 *
 * @PreAuthorize method annotations validate that user has authorization to access the given resource.
 */
@Service
public class HeartService {
    private final HeartRepository heartRepository;
    private final DogRepository dogRepository;

    public HeartService(HeartRepository heartRepository, DogRepository dogRepository) {
        this.heartRepository = heartRepository;
        this.dogRepository = dogRepository;
    }

    /**
     * Adds a new heart record for a given dog.
     *
     * @param dogId   The dog to add a heart record for.
     * @param request The heart record request.
     * @return on Optional object of HeartResponse containing the saved object, or an empty Optional in case of an
     * error.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId)")
    public Optional<HeartResponse> addHeartStatus(String dogId, HeartRequest request) {
        DogEntity dog = dogRepository.findById(dogId).orElse(null);
        if (dog == null) return Optional.empty();

        HeartEntity entity = HeartEntity.builder()
                .dog(dog)
                .hasFatigue(request.getFatigue())
                .isCoughing(request.getCoughing())
                .murmursStatus(request.getMurmurStatus())
                .heartRate(request.getHeartRate().intValue())
                .breathingRate(request.getBreathingRate().intValue())
                .lastDirofilariaLastPrevention(request.getLastDirofilariaPreventionDate())
                .createdTs(LocalDateTime.now())
                .build();

        HeartEntity saved = heartRepository.save(entity);
        return Optional.of(toResponse(saved));
    }

    /**
     * Retrieves a list of all heart records for a given dog.
     *
     * @param dogId The dog id to retrieve a list of heart statuses for.
     * @return A list of HeartResponse containing all heart records for a given dog.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId)")
    public List<HeartResponse> getHeartStatuses(String dogId) {
        return heartRepository.findByDog_Id(dogId)
                .stream()
                .map(this::toResponse)
                .map(heartResponse -> heartResponse
                        .status(calculateHeartStatus(heartResponse))
                        .healthHighlights(addHealthHighlights(heartResponse))
                )
                .collect(Collectors.toList());
    }

    /**
     * Calculates the health status (RED, GREEN or YELLOW) of the heart entity based on the values of its properties.
     * Ignores a given property if null, sums all properties with data in the totalMetrics variable, and all properties
     * with good health score in the healthyCount variable. Calculates an overall health status based on the ratio
     * between the two values.
     *
     * @param heartResponse The heartResponse to analyze the status for.
     * @return QuizCategoryStatus of color representing the status of the heart.
     */
    private QuizCategoryStatus calculateHeartStatus(HeartResponse heartResponse) {

        int totalMetrics = 0; // how many of the properties of QuizCategoryStatus are not null
        int healthyCount = 0; // how many of the properties of QuizCategoryStatus are considered "healthy"

        if (heartResponse.getFatigue() != null) {
            totalMetrics++;
            if (!heartResponse.getFatigue()) {
                healthyCount++;
            }
        }

        if (heartResponse.getCoughing() != null) {
            totalMetrics++;
            if (!heartResponse.getCoughing()) {
                healthyCount++;
            }
        }

        if (heartResponse.getMurmurStatus() != null) {
            totalMetrics++;
            MurmurStatus murmur = heartResponse.getMurmurStatus();
            if (murmur == MurmurStatus.NONE || murmur == MurmurStatus.GRADE_I) {
                healthyCount++;
            }
        }

        if (heartResponse.getHeartRate() != null) {
            totalMetrics++;
            float hr = heartResponse.getHeartRate().floatValue();
            if (hr >= 60 && hr <= 120) {
                healthyCount++;
            }
        }

        if (heartResponse.getBreathingRate() != null) {
            totalMetrics++;
            float br = heartResponse.getBreathingRate().floatValue();
            if (br >= 10 && br <= 35) {
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

    private List<HealthHighlight> addHealthHighlights(HeartResponse heartResponse) {

        // check sth
        // check dirofilaria

        return Collections.emptyList();
    }

    /**
     * Retrieves a requested heart record for a given dog by id.
     *
     * @param dogId   The dog id to retrieve a heart status for.
     * @param heartId The id of the HeartEntity to retrieve.
     * @return an Optional object of HeartResponse containing the requested object, or an empty Optional in case of an
     * error
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId) && @authorizationService.hasHeartRecordOwnership(#dogId, #heartId)")
    public Optional<HeartResponse> getHeartStatusById(String dogId, String heartId) {
        return heartRepository.findById(heartId)
                .filter(e -> e.getDog() != null && dogId.equals(e.getDog().getId()))
                .map(this::toResponse);
    }

    /**
     * Updates a given heart record for a given dog in the database.
     *
     * @param dogId   The dog id to update a heart status for.
     * @param heartId The id of the HeartEntity to update.
     * @param request The HeartRequest containing the new values to assign to the HeartEntity.
     * @return an Optional object of the HeartResponse with updated values, or an empty Optional in case of an error.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId) && @authorizationService.hasHeartRecordOwnership(#dogId, #heartId)")
    public Optional<HeartResponse> updateHeartStatus(String dogId, String heartId, HeartRequest request) {
        return heartRepository.findById(heartId)
                .filter(e -> e.getDog() != null && dogId.equals(e.getDog().getId()))
                .map(entity -> {
                    entity.setHasFatigue(request.getFatigue());
                    entity.setIsCoughing(request.getCoughing());
                    entity.setMurmursStatus(request.getMurmurStatus());
                    entity.setHeartRate(request.getHeartRate().intValue());
                    entity.setBreathingRate(request.getBreathingRate().intValue());
                    entity.setLastDirofilariaLastPrevention(request.getLastDirofilariaPreventionDate()); //TODO: add null check
                    entity.setCreatedTs(LocalDateTime.now());
                    HeartEntity saved = heartRepository.save(entity);
                    return toResponse(saved);
                });
    }

    /**
     * Deletes a given heart record of a given dog, if exists.
     *
     * @param dogId   The dog id to delete a heart status for.
     * @param heartId The id of the HeartEntity to delete.
     * @return true if heart record was deleted successfully, false otherwise.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId) && @authorizationService.hasHeartRecordOwnership(#dogId, #heartId)")
    public boolean deleteHeartStatus(String dogId, String heartId) {
        return heartRepository.findById(heartId)
                .filter(e -> e.getDog() != null && dogId.equals(e.getDog().getId()))
                .map(e -> {
                    heartRepository.delete(e);
                    return true;
                }).orElse(false);
    }

    /**
     * Maps the HeartEntity object to a HeartResponse object containing the heart record information.
     *
     * @param entity The HeartEntity object to map to HeartResponse.
     * @return HeartResponse object.
     */
    private HeartResponse toResponse(HeartEntity entity) {
        HeartResponse resp = new HeartResponse();
        resp.setHeartId(entity.getId());
        resp.setDogId(entity.getDog() != null ? entity.getDog().getId() : null);
        resp.setFatigue(entity.getHasFatigue());
        resp.setCoughing(entity.getIsCoughing());
        resp.setMurmurStatus(entity.getMurmursStatus());
        resp.setHeartRate(BigDecimal.valueOf(entity.getHeartRate()));
        resp.setBreathingRate(BigDecimal.valueOf(entity.getBreathingRate()));
        resp.setLastDirofilariaPreventionDate(entity.getLastDirofilariaLastPrevention());
        resp.setCreatedTs(entity.getCreatedTs().atOffset(ZoneOffset.UTC));
        return resp;
    }
}
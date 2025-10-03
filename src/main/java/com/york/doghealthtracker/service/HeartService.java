package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.entity.HeartEntity;
import com.york.doghealthtracker.model.HeartRequest;
import com.york.doghealthtracker.model.HeartResponse;
import com.york.doghealthtracker.repository.DogRepository;
import com.york.doghealthtracker.repository.HeartRepository;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
                .collect(Collectors.toList());
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
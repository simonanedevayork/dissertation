package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.entity.HeartEntity;
import com.york.doghealthtracker.model.HeartRequest;
import com.york.doghealthtracker.model.HeartResponse;
import com.york.doghealthtracker.repository.DogRepository;
import com.york.doghealthtracker.repository.HeartRepository;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HeartService {

    private final HeartRepository heartRepository;
    private final DogRepository dogRepository;

    public HeartService(HeartRepository heartRepository, DogRepository dogRepository) {
        this.heartRepository = heartRepository;
        this.dogRepository = dogRepository;
    }

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

    public List<HeartResponse> getHeartStatuses(String dogId) {
        return heartRepository.findByDog_Id(dogId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<HeartResponse> getHeartStatusById(String dogId, String heartId) {
        return heartRepository.findById(heartId)
                .filter(e -> e.getDog() != null && dogId.equals(e.getDog().getId()))
                .map(this::toResponse);
    }

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

    public boolean deleteHeartStatus(String dogId, String heartId) {
        return heartRepository.findById(heartId)
                .filter(e -> e.getDog() != null && dogId.equals(e.getDog().getId()))
                .map(e -> {
                    heartRepository.delete(e);
                    return true;
                }).orElse(false);
    }

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
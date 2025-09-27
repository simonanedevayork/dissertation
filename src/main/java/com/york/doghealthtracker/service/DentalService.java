package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.DentalEntity;
import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.model.DentalRequest;
import com.york.doghealthtracker.model.DentalResponse;
import com.york.doghealthtracker.model.PlaqueStatus;
import com.york.doghealthtracker.repository.DentalRepository;
import com.york.doghealthtracker.repository.DogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DentalService {

    private final DentalRepository dentalRepository;
    private final DogRepository dogRepository;

    public DentalService(DentalRepository dentalRepository, DogRepository dogRepository) {
        this.dentalRepository = dentalRepository;
        this.dogRepository = dogRepository;
    }

    public Optional<DentalResponse> addDentalStatus(String dogId, DentalRequest request) {
        DogEntity dog = dogRepository.findById(dogId).orElse(null);
        if (dog == null) return Optional.empty();

        DentalEntity entity =  DentalEntity.builder()
                .dog(dog)
                .plaqueStatus(request.getPlaqueStatus())
                .toothLoss(request.getToothLoss())
                .gingivitisStatus(request.getGingivitisStatus())
                .lastCleaningDate(request.getLastCleaningDate())
                .severityScore(calculateSeverityScore(request))
                .createdTs(LocalDateTime.now())
                .build();

        DentalEntity saved = dentalRepository.save(entity);
        return Optional.of(toResponse(saved));
    }

    private Integer calculateSeverityScore(DentalRequest request) {

        // TODO: calculate the severity score -- the teeth INDEX
        /*
         request.getPlaqueStatus() == PlaqueStatus.HI
         request.getPlaqueStatus() == PlaqueStatus.NORM
         request.getPlaqueStatus() == PlaqueStatus.LOW

         request.getToothLoss() == true
         request.getToothLoss() == false

         request.getGingivitisStatus() == GingivitisStatus.SEVERE
         request.getGingivitisStatus() == GingivitisStatus.MILD
         request.getGingivitisStatus() == GingivitisStatus.NONE
         */

        return null;
    }

    public List<DentalResponse> getDentalStatuses(String dogId) {
        return dentalRepository.findByDog_Id(dogId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<DentalResponse> getDentalStatusById(String dogId, String dentalId) {
        return dentalRepository.findById(dentalId)
                .filter(e -> e.getDog() != null && dogId.equals(e.getDog().getId()))
                .map(this::toResponse);
    }

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
                    return toResponse(saved);
                });
    }

    public boolean deleteDentalStatus(String dogId, String dentalId) {
        return dentalRepository.findById(dentalId)
                .filter(e -> e.getDog() != null && dogId.equals(e.getDog().getId()))
                .map(e -> {
                    dentalRepository.delete(e);
                    return true;
                }).orElse(false);
    }

    private DentalResponse toResponse(DentalEntity entity) {
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
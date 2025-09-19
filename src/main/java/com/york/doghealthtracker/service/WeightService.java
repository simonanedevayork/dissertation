package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.WeightEntity;
import com.york.doghealthtracker.model.WeightRequest;
import com.york.doghealthtracker.model.WeightResponse;
import com.york.doghealthtracker.repository.DogRepository;
import com.york.doghealthtracker.repository.WeightRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class WeightService {

    private final WeightRepository weightRepository;
    private final DogRepository dogRepository;

    public WeightService(WeightRepository weightRepository, DogRepository dogRepository) {
        this.weightRepository = weightRepository;
        this.dogRepository = dogRepository;
    }

    public Optional<WeightResponse> addWeight(String dogId, WeightRequest request) {
        return dogRepository.findById(dogId).map(dog -> {
            WeightEntity entity = new WeightEntity();
            entity.setDog(dog);
            entity.setCurrent(request.getCurrent());
            entity.setCreatedTs(LocalDateTime.now());
            WeightEntity saved = weightRepository.save(entity);
            return mapToResponse(saved);
        });
    }

    public List<WeightResponse> getWeights(String dogId) {
        return weightRepository.findByDogId(dogId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public boolean deleteWeight(String dogId, String weightId) {
        return weightRepository.findById(weightId)
                .filter(weight -> weight.getDog().getId().equals(dogId))
                .map(weight -> {
                    weightRepository.delete(weight);
                    return true;
                })
                .orElse(false);
    }

    private WeightResponse mapToResponse(WeightEntity entity) {

        WeightResponse resp = new WeightResponse();
        resp.setId(entity.getId());
        resp.setDogId(entity.getDog().getId());
        resp.setCurrent(entity.getCurrent());
        resp.setDate(LocalDateTime.now().atOffset(ZoneOffset.UTC)); // TODO: see conversion and fix
        return resp;
    }
}
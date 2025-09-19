package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.entity.UserEntity;
import com.york.doghealthtracker.model.DogRequest;
import com.york.doghealthtracker.model.DogResponse;
import com.york.doghealthtracker.repository.DogRepository;
import com.york.doghealthtracker.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;

@Service
public class DogService {

    private final DogRepository dogRepository;
    private final UserRepository userRepository;

    public DogService(DogRepository dogRepository, UserRepository userRepository) {
        this.dogRepository = dogRepository;
        this.userRepository = userRepository;
    }

    public DogResponse createDog(DogRequest request) {

        String participantEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> owner = userRepository.findByEmail(participantEmail);

        DogEntity entity = new DogEntity();
        entity.setOwner(owner.get());
        entity.setName(request.getName());
        entity.setGender(request.getGender());
        entity.setBreed(request.getBreed());
        entity.setBirthDate(request.getBirthDate());
        entity.setPhoto(String.valueOf(request.getPhotoUrl()));
        entity.setIsNeutered(request.getIsNeutered());

        DogEntity saved = dogRepository.save(entity);
        return mapToResponse(saved);
    }

    public Optional<DogResponse> getDogById(String dogId) {
        return dogRepository.findById(dogId).map(this::mapToResponse);
    }

    public Optional<DogResponse> updateDog(String dogId, DogRequest request) {
        return dogRepository.findById(dogId).map(entity -> {
            entity.setName(request.getName());
            entity.setGender(request.getGender());
            entity.setBreed(request.getBreed());
            entity.setBirthDate(request.getBirthDate());
            entity.setPhoto(String.valueOf(request.getPhotoUrl()));
            entity.setIsNeutered(request.getIsNeutered());
            dogRepository.save(entity);
            return mapToResponse(entity);
        });
    }

    private DogResponse mapToResponse(DogEntity entity) {
        DogResponse resp = new DogResponse();
        resp.setDogId(entity.getId());
        resp.setOwnerId(entity.getOwner().getId());
        resp.setName(entity.getName());
        resp.setGender(entity.getGender());
        resp.setBreed(entity.getBreed());
        resp.setBirthDate(entity.getBirthDate());
        resp.setPhotoUrl(URI.create(entity.getPhoto()));
        resp.setIsNeutered(entity.getIsNeutered());
        return resp;
    }

    public Optional<DogResponse> findDogByOwnerId(String ownerId) {
        return dogRepository.findByOwnerId(ownerId)
                .map(this::mapToResponse);
    }
}
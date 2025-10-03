package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.entity.UserEntity;
import com.york.doghealthtracker.exception.InvalidDogException;
import com.york.doghealthtracker.model.DogRequest;
import com.york.doghealthtracker.model.DogResponse;
import com.york.doghealthtracker.repository.DogRepository;
import com.york.doghealthtracker.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;

/**
 * Service responsible for dog management.
 *
 * @PreAuthorize method annotations validate that user has authorization to access the given resource.
 */
@Service
@Log4j2
public class DogService {

    private final DogRepository dogRepository;
    private final UserRepository userRepository;

    public DogService(DogRepository dogRepository, UserRepository userRepository) {
        this.dogRepository = dogRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new dog entity for a given participant and saves it in the database. Validates that the user
     * in context has no current dog, as a user can have only one dog.
     *
     * @param request The DogRequest containing the dog data.
     * @return DogResponse containing the dog data.
     * @throws InvalidDogException if user in context is invalid, or if user in context already has a dog.
     */
    public DogResponse createDog(DogRequest request) {

        String participantEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> owner = userRepository.findByEmail(participantEmail);

        if (owner.isPresent() && !ownerHasDog(owner.get().getId())) {

            DogEntity entity = new DogEntity();
            entity.setOwner(owner.get());
            entity.setName(request.getName());
            entity.setGender(request.getGender());
            entity.setBreed(request.getBreed());
            entity.setBirthDate(request.getBirthDate());
            entity.setPhoto(String.valueOf(request.getPhotoUrl()));
            entity.setIsNeutered(request.getIsNeutered());

            DogEntity saved = dogRepository.save(entity);
            return mapToDogResponse(saved);
        } else {
            log.error("Unsuccessful dog creation.");
            throw new InvalidDogException("Invalid dog creation.");
        }
    }

    /**
     * Validates if the given user already has a dog in the database.
     *
     * @param participantId The user id to validate dog presence for.
     * @return True if user already has dog in the database, false otherwise.
     */
    public boolean ownerHasDog(String participantId) {
        return dogRepository.findByOwnerId(participantId)
                .map(dog -> {
                    log.warn("User with id: {} already has a dog in the database.", participantId);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Retrieves a requested dog by id.
     *
     * @param dogId The dog id identifying the dog to retrieve.
     * @return Optional of DogResponse, empty optional if dog not present in database.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId)")
    public Optional<DogResponse> getDogById(String dogId) {
        return dogRepository.findById(dogId)
                .map(this::mapToDogResponse);
    }

    /**
     * Updates a given dog in the database.
     *
     * @param dogId   The dog id identifying the dog to update.
     * @param request The DogRequest containing the new values to assign to the DogEntity.
     * @return DogResponse with updated values.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId)")
    public Optional<DogResponse> updateDog(String dogId, DogRequest request) {
        return dogRepository.findById(dogId)
                .map(entity -> {
                    entity.setName(request.getName());
                    entity.setGender(request.getGender());
                    entity.setBreed(request.getBreed());
                    entity.setBirthDate(request.getBirthDate());
                    entity.setPhoto(String.valueOf(request.getPhotoUrl()));
                    entity.setIsNeutered(request.getIsNeutered());

                    dogRepository.save(entity);
                    return mapToDogResponse(entity);
                });
    }

    /**
     * Maps the DogEntity object to a DogResponse object containing the dog information.
     *
     * @param entity The DogEntity object to map to DogResponse.
     * @return DogResponse object.
     */
    private DogResponse mapToDogResponse(DogEntity entity) {
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

    /**
     * Retrieves the dog entity corresponding to the provided user id.
     *
     * @param participantId The user id to find a dog for.
     * @return Optional of DogResponse object, empty optional if dog is not present.
     */
    public Optional<DogResponse> findDogByOwnerId(String participantId) {
        return dogRepository.findByOwnerId(participantId)
                .map(this::mapToDogResponse);
    }
}
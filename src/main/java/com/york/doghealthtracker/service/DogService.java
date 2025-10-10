package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.entity.UserEntity;
import com.york.doghealthtracker.exception.InvalidDogException;
import com.york.doghealthtracker.model.DogResponse;
import com.york.doghealthtracker.model.Gender;
import com.york.doghealthtracker.repository.DogRepository;
import com.york.doghealthtracker.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
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
    private final FileStorageService fileStorageService;

    @Value("${app.base-url}")
    private String baseUrl;

    public DogService(DogRepository dogRepository, UserRepository userRepository, FileStorageService fileStorageService) {
        this.dogRepository = dogRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Creates a new dog entity for a given participant and saves it in the database. Validates that the user
     * in context has no current dog, as a user can have only one dog.
     *
     * @param name
     * @param gender
     * @param breed
     * @param birthDate
     * @param isNeutered
     * @param file
     * @return DogResponse containing the dog data.
     * @throws IOException
     * @throws InvalidDogException if user in context is invalid, or if user in context already has a dog.
     */
    public DogResponse createDog(String name, Gender gender, String breed, LocalDate birthDate, Boolean isNeutered, MultipartFile file) throws IOException {

        String participantEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> owner = userRepository.findByEmail(participantEmail);

        if (owner.isPresent() && !ownerHasDog(owner.get().getId())) {

            DogEntity entity = new DogEntity();
            entity.setOwner(owner.get());
            entity.setName(name);
            entity.setGender(gender);
            entity.setBreed(breed);
            entity.setBirthDate(birthDate);
            entity.setIsNeutered(isNeutered);

            if (file != null && !file.isEmpty()) {
                String storedFilename = fileStorageService.store(owner.get().getId(), file);
                String fileUrl = String.format("%s/uploads/%s/%s", baseUrl, owner.get().getId(), storedFilename);
                entity.setPhoto(fileUrl);
            } else {
                entity.setPhoto(null);
            }

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
     * @param dogId The dog id identifying the dog to update.
     *              //TODO: add docs
     * @return DogResponse with updated values.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId)")
    public Optional<DogResponse> updateDog(String dogId,
                                           String name,
                                           Gender gender,
                                           String breed,
                                           LocalDate birthDate,
                                           Boolean isNeutered,
                                           MultipartFile file) throws IOException {
        return dogRepository.findById(dogId)
                .map(entity -> {
                    entity.setName(name);
                    entity.setGender(gender);
                    entity.setBreed(breed);
                    entity.setBirthDate(birthDate);
                    entity.setIsNeutered(isNeutered);

                    if (file != null && !file.isEmpty()) {
                        try {
                            String ownerId = entity.getOwner().getId();
                            String storedFilename = fileStorageService.store(ownerId, file);
                            String fileUrl = String.format("%s/uploads/%s/%s", baseUrl, ownerId, storedFilename);
                            entity.setPhoto(fileUrl);
                            log.info("Updated dog photo for dogId: {}", dogId);
                        } catch (Exception e) {
                            log.error("Failed to update photo for dogId: {}", dogId);
                            throw new InvalidDogException("Photo update failed");
                        }
                    }

                    DogEntity updated = dogRepository.save(entity);
                    return mapToDogResponse(updated);
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
        resp.setIsNeutered(entity.getIsNeutered());

        if (entity.getPhoto() != null) {
            resp.setPhotoUrl(URI.create(entity.getPhoto()));
        }

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
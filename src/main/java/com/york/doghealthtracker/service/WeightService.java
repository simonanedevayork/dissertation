package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.WeightEntity;
import com.york.doghealthtracker.model.WeightRequest;
import com.york.doghealthtracker.model.WeightResponse;
import com.york.doghealthtracker.repository.DogRepository;
import com.york.doghealthtracker.repository.WeightRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * Service responsible for dog weight management.
 *
 * @PreAuthorize method annotations validate that user has authorization to access the given resource.
 */
@Service
public class WeightService {
    private final WeightRepository weightRepository;
    private final DogRepository dogRepository;

    public WeightService(WeightRepository weightRepository, DogRepository dogRepository) {
        this.weightRepository = weightRepository;
        this.dogRepository = dogRepository;
    }

    /**
     * Adds a new weight status for a given dog.
     *
     * @param dogId   The dog to add a dental status for.
     * @param request The dental status request.
     * @returnan Optional object of WeightResponse containing the saved object, or an empty Optional in case of an
     * error.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId)")
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

    /**
     * Retrieves a list of all weight statuses for a given dog.
     *
     * @param dogId The dog id to retrieve a list of weight statuses for.
     * @return a list of WeightResponse containing all dental statuses for the given dog.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId)")
    public List<WeightResponse> getWeights(String dogId) {
        return weightRepository.findByDogId(dogId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Retrieves the most recent weight entity from the database.
     *
     * @return Optional of WeightEntity representing the most recent weight added, or empty optional if no such value is
     * present.
     */
    public Optional<WeightEntity> getCurrentWeightEntity(String dogId) {
        return weightRepository.findTopByDog_IdOrderByCreatedTsDesc(dogId);
    }

    /**
     * Deletes a given weight status of a given dog, if exists.
     *
     * @param dogId    The dog id to delete weight status for.
     * @param weightId The weight status to delete.
     * @return true if weight status was deleted successfully, false otherwise.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId) && @authorizationService.hasDentalStatusOwnership(#dogId, #dentalId)")
    public boolean deleteWeight(String dogId, String weightId) {
        return weightRepository.findById(weightId)
                .filter(weight -> weight.getDog().getId().equals(dogId))
                .map(weight -> {
                    weightRepository.delete(weight);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Maps the WeightEntity object to a WeightResponse object containing the dental status information.
     *
     * @param entity The WeightEntity object to map to WeightResponse.
     * @return WeightResponse object.
     */
    private WeightResponse mapToResponse(WeightEntity entity) {

        WeightResponse resp = new WeightResponse();
        resp.setId(entity.getId());
        resp.setDogId(entity.getDog().getId());
        resp.setCurrent(entity.getCurrent());
        resp.setDate(entity.getCreatedTs().atOffset(ZoneOffset.UTC));
        return resp;
    }
}
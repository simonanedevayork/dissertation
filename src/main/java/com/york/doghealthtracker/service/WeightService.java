package com.york.doghealthtracker.service;

import com.york.doghealthtracker.config.DogConfig;
import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.entity.WeightEntity;
import com.york.doghealthtracker.exception.InvalidDogException;
import com.york.doghealthtracker.model.*;
import com.york.doghealthtracker.repository.DogRepository;
import com.york.doghealthtracker.repository.WeightRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private final DogConfig dogConfig;

    public WeightService(WeightRepository weightRepository, DogRepository dogRepository, DogConfig dogConfig) {
        this.weightRepository = weightRepository;
        this.dogRepository = dogRepository;
        this.dogConfig = dogConfig;
    }

    /**
     * Adds a new weight status for a given dog.
     *
     * @param dogId   The dog to add a dental status for.
     * @param request The dental status request.
     * @return an Optional object of WeightResponse containing the saved object, or an empty Optional in case of an
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

        Optional<DogEntity> dogOpt = dogRepository.findById(dogId);

        if (dogOpt.isPresent()) {

            String breed = dogOpt.get().getBreed();
            Map<String, Float> range = getWeightRangeForBreed(breed);

            List<WeightEntity> weights = weightRepository.findByDogId(dogId);

            if (weights == null || weights.isEmpty()) {
                WeightResponse response = new WeightResponse()
                        .current(null)
                        .goalWeightRange(mapToGoalWeightRange(range))
                        .status(null)
                        .healthHighlights(Collections.emptyList());
                return List.of(response);
            }

            return weights.stream()
                    .map(this::mapToResponse)
                    .map(weightResponse -> weightResponse
                            .goalWeightRange(mapToGoalWeightRange(range))
                            .healthHighlights(addHealthHighlights(dogId))
                    )
                    .map(weightResponse -> weightResponse
                            .status(calculateWeightStatus(weightResponse)))
                    .toList();
        } else {
            throw new InvalidDogException("Invalid dog.");
        }
    }

    /**
     * Retrieves the weight range for a given breed from the application.yml configuration.
     *
     * @param breed The dog breed to retrieve weight range for.
     * @return a Map of the min and max strings with their corresponding values.
     * @throws InvalidDogException if there is no such breed configured.
     */
    private Map<String, Float> getWeightRangeForBreed(String breed) {
        try {
            return dogConfig.getGoalWeightRanges().get(breed);
        } catch (RuntimeException e) {
            throw new InvalidDogException("Invalid dog breed.");
        }
    }

    /**
     * Maps the provided min and max weight range values to a GoalWeightRange object.
     *
     * @param range The min and max range to map.
     * @return a GoalWeightRange object with corresponding min and max values.
     */
    private GoalWeightRange mapToGoalWeightRange(Map<String, Float> range) {
        return new GoalWeightRange()
                .min(range.getOrDefault("min", 0.0f))
                .max(range.getOrDefault("max", 0.0f));
    }

    /**
     * Calculates the health status (RED, GREEN or YELLOW) of the weight entity based on its value compared to a given
     * range. Allows a 5 percent (0.05) tolerance above and below the given range, which is represented though a YELLOW
     * category.
     *
     * @param weightResponse The weightResponse to analyze the status for.
     * @return QuizCategoryStatus of color representing the status of the weight
     */
    private QuizCategoryStatus calculateWeightStatus(WeightResponse weightResponse) {

        GoalWeightRange range = weightResponse.getGoalWeightRange();
        float min = range.getMin();
        float max = range.getMax();

        float currentWeight = weightResponse.getCurrent();

        float tolerance = 0.05f * (max - min);
        float lowerBound = min - tolerance;
        float upperBound = max + tolerance;

        if (currentWeight >= min && currentWeight <= max) {
            return QuizCategoryStatus.GREEN;
        } else if (currentWeight >= lowerBound && currentWeight <= upperBound) {
            return QuizCategoryStatus.YELLOW;
        } else {
            return QuizCategoryStatus.RED;
        }
    }

    private List<HealthHighlight> addHealthHighlights(String dogId) {
        // calculate the highlights

        //TODO: implement
        return Collections.emptyList();
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
    public void deleteWeight(String weightId) {
        weightRepository.deleteById(weightId);
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
        resp.setCurrent(entity.getCurrent());
        resp.setDate(entity.getCreatedTs().atOffset(ZoneOffset.UTC));
        return resp;
    }
}
package com.york.doghealthtracker.service.security;

import com.york.doghealthtracker.entity.UserEntity;
import com.york.doghealthtracker.exception.AccessDeniedException;
import com.york.doghealthtracker.repository.DentalRepository;
import com.york.doghealthtracker.repository.DogRepository;
import com.york.doghealthtracker.repository.HealthRecordRepository;
import com.york.doghealthtracker.repository.HeartRepository;
import org.springframework.stereotype.Service;

/**
 * Centralizes authorization logic for resource access.
 */
@Service("authorizationService")
public class AuthorizationService {

    private final DogRepository dogRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final DentalRepository dentalRepository;
    private final HeartRepository heartRepository;
    private final UserContextService userContextService;

    public AuthorizationService(DogRepository dogRepository, HealthRecordRepository healthRecordRepository, DentalRepository dentalRepository, HeartRepository heartRepository, UserContextService userContextService) {
        this.dogRepository = dogRepository;
        this.healthRecordRepository = healthRecordRepository;
        this.dentalRepository = dentalRepository;
        this.heartRepository = heartRepository;
        this.userContextService = userContextService;
    }

    /**
     * Validates if provided dogId belongs to the user in context.
     *
     * @param dogId The dog id to validate.
     * @throws AccessDeniedException if dog does not belong to the user in context.
     */
    public void hasDogOwnership(String dogId) {
        UserEntity userInContext = userContextService.getUserInContext();
        boolean ownsDog = dogRepository.existsByIdAndOwner_Id(dogId, userInContext.getId());

        if (!ownsDog) {
            throw new AccessDeniedException(
                    String.format("Unauthorized access. User %s does not have ownership of dog %s", userInContext.getId(), dogId)
            );
        }
    }

    /**
     * Validates if provided dentalStatusId belongs to the provided dog.
     *
     * @param dogId          The dog id to validate.
     * @param dentalStatusId The dental status id to validate.
     * @throws AccessDeniedException if dental status does not belong to the dog.
     */
    public void hasDentalStatusOwnership(String dogId, String dentalStatusId) {
        boolean ownsDentalStatus = dentalRepository.existsByIdAndDog_Id(dentalStatusId, dogId);

        if (!ownsDentalStatus) {
            throw new AccessDeniedException(
                    String.format("Unauthorized access. Dental status with id: %s does not belong to dog with id: %s", dentalStatusId, dogId)
            );
        }
    }

    /**
     * Validates if provided healthRecordId belongs to the provided dog.
     *
     * @param dogId          The dog id to validate.
     * @param healthRecordId The health record id to validate.
     * @throws AccessDeniedException if health record does not belong to the dog.
     */
    public void hasHealthRecordOwnership(String dogId, String healthRecordId) {
        boolean ownsHealthRecord = healthRecordRepository.existsByIdAndDog_Id(healthRecordId, dogId);

        if (!ownsHealthRecord) {
            throw new AccessDeniedException(
                    String.format("Unauthorized access. Health record with id: %s does not belong to dog with id: %s", healthRecordId, dogId)
            );
        }
    }

    /**
     * Validates if provided heartId belongs to the provided dog.
     *
     * @param dogId   The dog id to validate.
     * @param heartId The heart record id to validate.
     * @throws AccessDeniedException if heart record does not belong to the dog.
     */
    public void hasHeartRecordOwnership(String dogId, String heartId) {
        boolean ownsHealthRecord = heartRepository.existsByIdAndDog_Id(heartId, dogId);

        if (!ownsHealthRecord) {
            throw new AccessDeniedException(
                    String.format("Unauthorized access. Heart record with id: %s does not belong to dog with id: %s", heartId, dogId)
            );
        }
    }

}

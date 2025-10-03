package com.york.doghealthtracker.repository;

import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DogRepository extends JpaRepository<DogEntity, String> {
    Optional<DogEntity> findByOwnerId(String participantId);
    Boolean existsByIdAndOwner_Id(String dogId, String participantId);
}

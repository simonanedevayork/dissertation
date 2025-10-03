package com.york.doghealthtracker.repository;

import com.york.doghealthtracker.entity.HealthRecordEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HealthRecordRepository extends JpaRepository<HealthRecordEntity, String> {
    List<HealthRecordEntity> findByDog_Id(String dogId);
    @NotNull
    Optional<HealthRecordEntity> findById(@NotNull String id);
    Boolean existsByIdAndDog_Id(String healthRecordId, String dogId);
}

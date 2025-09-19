package com.york.doghealthtracker.repository;

import com.york.doghealthtracker.entity.WeightEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeightRepository extends JpaRepository<WeightEntity, String> {
    List<WeightEntity> findByDogId(String dogId);
}
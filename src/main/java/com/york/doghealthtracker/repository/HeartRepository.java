package com.york.doghealthtracker.repository;

import com.york.doghealthtracker.entity.DentalEntity;
import com.york.doghealthtracker.entity.HeartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HeartRepository extends JpaRepository<HeartEntity, String> {
    List<HeartEntity> findByDog_Id(String dogId);
}

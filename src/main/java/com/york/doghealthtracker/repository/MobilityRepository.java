package com.york.doghealthtracker.repository;

import com.york.doghealthtracker.entity.HormoneEntity;
import com.york.doghealthtracker.entity.MobilityEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MobilityRepository extends JpaRepository<MobilityEntity, String> {
    List<MobilityEntity> findByDog_Id(String dogId);
    @Transactional
    void deleteByDog_Id(String dogId);
}

package com.york.doghealthtracker.repository;

import com.york.doghealthtracker.entity.HormoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HormoneRepository extends JpaRepository<HormoneEntity, String> {
   List<HormoneEntity> findByDog_Id(String dogId);
    void deleteByDog_Id(String dogId);
}

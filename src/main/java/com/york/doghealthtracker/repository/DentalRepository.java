package com.york.doghealthtracker.repository;

import com.york.doghealthtracker.entity.DentalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DentalRepository extends JpaRepository<DentalEntity, String> {

    List<DentalEntity> findByDog_Id(String dogId);

}

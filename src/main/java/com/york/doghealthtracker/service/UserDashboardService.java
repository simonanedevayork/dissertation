package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.WeightEntity;
import com.york.doghealthtracker.model.DashboardResponse;
import com.york.doghealthtracker.model.HealthHighlight;
import com.york.doghealthtracker.model.HormoneStatusResponse;
import com.york.doghealthtracker.model.MobilityStatusResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class UserDashboardService {

    private final WeightService weightService;
    private final HormoneService hormoneService;
    private final MobilityService mobilityService;
    private final DentalService dentalService;
    private final HeartService heartService;
    private final HealthRecordService healthRecordService;

    public UserDashboardService(WeightService weightService, HormoneService hormoneService, MobilityService mobilityService, DentalService dentalService, HeartService heartService, HealthRecordService healthRecordService) {
        this.weightService = weightService;
        this.hormoneService = hormoneService;
        this.mobilityService = mobilityService;
        this.dentalService = dentalService;
        this.heartService = heartService;
        this.healthRecordService = healthRecordService;
    }

    public DashboardResponse getDashboard(String userId, String dogId) {
        return new DashboardResponse()
                .currentWeight(getCurrentWeight(dogId))
                .hormonesStatus(getHormonesStatus(dogId))
                .mobilityStatus(getMobilityStatus(dogId))
                .totalDentalRecords(getTotalDentalRecords(dogId))
                .totalHeartRecords(getTotalHealthRecords(dogId))
                .healthHighlights(generateHealthHighlights());
    }

    /**
     * Retrieves the most recent weight entity and displays its value, or zero if no such weight has been added.
     *
     * @param dogId The id of the dog to get most current weight for.
     * @return Flot number representing the most recent weight entity of the dog, or zero is no such entity exists.
     */
    public Float getCurrentWeight(String dogId) {
        return weightService.getCurrentWeightEntity(dogId)
                .map(WeightEntity::getCurrent)
                .orElse(0.00f);
    }

    public HormoneStatusResponse getHormonesStatus(String dogId) {
        return hormoneService.getHormoneStatusResponse(dogId);
    }

    public MobilityStatusResponse getMobilityStatus(String dogId) {
        return mobilityService.getMobilityStatusResponse(dogId);
    }

    public BigDecimal getTotalDentalRecords(String dogId) {
        return BigDecimal.valueOf(
                dentalService.getDentalListResponse(dogId).getDentalRecords().size()
        );
    }

    public BigDecimal getTotalHeartRecords(String dogId) {
        return BigDecimal.valueOf(
                heartService.getHeartStatuses(dogId).size()
        );
    }

    public BigDecimal getTotalHealthRecords(String dogId) {
        return BigDecimal.valueOf(
                healthRecordService.getHealthRecords(dogId).size()
        );
    }

    private List<HealthHighlight> generateHealthHighlights() {
        return Collections.emptyList();
    }

}

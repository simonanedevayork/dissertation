package com.york.doghealthtracker.service;

import com.york.doghealthtracker.config.HighlightConfig;
import com.york.doghealthtracker.entity.WeightEntity;
import com.york.doghealthtracker.model.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Log4j2
public class UserDashboardService {

    private final WeightService weightService;
    private final HormoneService hormoneService;
    private final MobilityService mobilityService;
    private final DentalService dentalService;
    private final HeartService heartService;
    private final HealthRecordService healthRecordService;
    private final HighlightConfig highlightConfig;

    public UserDashboardService(
            WeightService weightService,
            HormoneService hormoneService,
            MobilityService mobilityService,
            DentalService dentalService,
            HeartService heartService,
            HealthRecordService healthRecordService,
            HighlightConfig highlightConfig) {
        this.weightService = weightService;
        this.hormoneService = hormoneService;
        this.mobilityService = mobilityService;
        this.dentalService = dentalService;
        this.heartService = heartService;
        this.healthRecordService = healthRecordService;
        this.highlightConfig = highlightConfig;
    }

    public DashboardResponse getDashboard(String userId, String dogId) {
        try {
            return new DashboardResponse()
                    .currentWeight(getCurrentWeight(dogId))
                    .hormonesStatus(safeHormonesStatus(dogId))
                    .mobilityStatus(safeMobilityStatus(dogId))
                    .totalDentalRecords(safeCount(getTotalDentalRecords(dogId)))
                    .totalHeartRecords(safeCount(getTotalHeartRecords(dogId)))
                    .totalHealthRecords(safeCount(getTotalHealthRecords(dogId)))
                    .healthHighlights(generateHealthHighlights(dogId));
        } catch (Exception e) {
            log.error("Error building dashboard for dogId {}: {}", dogId, e.getMessage(), e);
            return new DashboardResponse()
                    .currentWeight(0.0f)
                    .hormonesStatus(new HormoneStatusResponse())
                    .mobilityStatus(new MobilityStatusResponse())
                    .totalDentalRecords(BigDecimal.ZERO)
                    .totalHeartRecords(BigDecimal.ZERO)
                    .totalHealthRecords(BigDecimal.ZERO)
                    .healthHighlights(Collections.emptyList());
        }
    }

    private Float getCurrentWeight(String dogId) {
        return weightService.getCurrentWeightEntity(dogId)
                .map(WeightEntity::getCurrent)
                .orElse(0.0f);
    }

    private HormoneStatusResponse safeHormonesStatus(String dogId) {
        try {
            return Optional.ofNullable(hormoneService.getHormoneStatusResponse(dogId))
                    .orElseGet(() -> {
                        log.warn("No hormone data found for dogId: {}", dogId);
                        return new HormoneStatusResponse();
                    });
        } catch (Exception e) {
            log.error("Failed to retrieve hormone status for {}: {}", dogId, e.getMessage());
            return new HormoneStatusResponse();
        }
    }

    private MobilityStatusResponse safeMobilityStatus(String dogId) {
        try {
            return Optional.ofNullable(mobilityService.getMobilityStatusResponse(dogId))
                    .orElseGet(() -> {
                        log.warn("No mobility data found for dogId: {}", dogId);
                        return new MobilityStatusResponse();
                    });
        } catch (Exception e) {
            log.error("Failed to retrieve mobility status for {}: {}", dogId, e.getMessage());
            return new MobilityStatusResponse();
        }
    }

    private BigDecimal safeCount(BigDecimal value) {
        return Optional.ofNullable(value).orElse(BigDecimal.ZERO);
    }

    private BigDecimal getTotalDentalRecords(String dogId) {
        try {
            var dentalResponse = dentalService.getDentalListResponse(dogId);
            if (dentalResponse == null || dentalResponse.getDentalRecords() == null)
                return BigDecimal.ZERO;
            return BigDecimal.valueOf(dentalResponse.getDentalRecords().size());
        } catch (Exception e) {
            log.error("Failed to count dental records for {}: {}", dogId, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getTotalHeartRecords(String dogId) {
        try {
            var heartStatuses = heartService.getHeartStatuses(dogId);
            return BigDecimal.valueOf(heartStatuses != null ? heartStatuses.size() : 0);
        } catch (Exception e) {
            log.error("Failed to count heart records for {}: {}", dogId, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getTotalHealthRecords(String dogId) {
        try {
            var records = healthRecordService.getHealthRecords(dogId);
            return BigDecimal.valueOf(records != null ? records.size() : 0);
        } catch (Exception e) {
            log.error("Failed to count health records for {}: {}", dogId, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private List<HealthHighlight> generateHealthHighlights(String dogId) {

        List<HealthHighlight> healthHighlights = new ArrayList<>();

        List<WeightResponse> weights = weightService.getWeights(dogId);

        WeightResponse latestWeight = weights.stream()
                .filter(w -> w.getDate() != null)
                .max(Comparator.comparing(WeightResponse::getDate))
                .orElse(null);

        if (latestWeight.getStatus() != null && latestWeight.getStatus() == QuizCategoryStatus.RED) {
            healthHighlights.add(constructHealthHighlight("overweightRisk"));
        }

        return healthHighlights;
    }

    private HealthHighlight constructHealthHighlight(String highlightType) {
        return highlightConfig.getMap().get(highlightType);
    }
}
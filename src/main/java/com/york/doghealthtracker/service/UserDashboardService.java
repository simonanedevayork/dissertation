package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.WeightEntity;
import com.york.doghealthtracker.model.DashboardResponse;
import com.york.doghealthtracker.model.HealthHighlight;
import com.york.doghealthtracker.model.QuizHighlight;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Log4j2
public class UserDashboardService {

    private final WeightService weightService;

    public UserDashboardService(WeightService weightService) {
        this.weightService = weightService;
    }

    public DashboardResponse getDashboard(String userId, String dogId) {
        return new DashboardResponse()
                .overallHealthIndex(calculateOverallHealthIndex())
                .currentWeight(getCurrentWeight(dogId))
                .yourProgress(calculateProgress())
                .healthHighlights(generateHealthHighlights())
                .quizHighlights(getQuizHighlights());
    }

    private BigDecimal calculateOverallHealthIndex() {

        return null;
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

    private Float calculateProgress() {
        return null;
    }

    private List<HealthHighlight> generateHealthHighlights() {
        return null;
    }

    private List<QuizHighlight> getQuizHighlights() {
        return null;
    }

}

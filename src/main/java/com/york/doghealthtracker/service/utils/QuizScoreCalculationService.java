package com.york.doghealthtracker.service.utils;

import com.york.doghealthtracker.config.HormoneQuizConfig;
import com.york.doghealthtracker.model.MobilityCategory;
import com.york.doghealthtracker.model.QuizAnswer;
import com.york.doghealthtracker.model.QuizCategoryStatus;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;

import static com.york.doghealthtracker.model.QuizAnswer.*;

@UtilityClass
public class QuizScoreCalculationService {

    public static int mapAnswerToScore(QuizAnswer answer) {
        return switch (answer) {
            case ALWAYS -> 3;
            case FREQUENTLY -> 2;
            case OCCASIONALLY -> 1;
            default -> 0;
        };
    }

    public static QuizCategoryStatus mapScoreToStatus(int score) {
        if (score <= 2) return QuizCategoryStatus.GREEN;
        if (score <= 5) return QuizCategoryStatus.YELLOW;
        return QuizCategoryStatus.RED;
    }

}

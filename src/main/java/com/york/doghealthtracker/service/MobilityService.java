package com.york.doghealthtracker.service;

import com.york.doghealthtracker.config.MobilityQuizConfig;
import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.entity.MobilityEntity;
import com.york.doghealthtracker.model.*;
import com.york.doghealthtracker.repository.MobilityRepository;
import com.york.doghealthtracker.service.utils.QuizScoreCalculationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This service...
 */
@Service
public class MobilityService {

    private final MobilityQuizConfig quizConfig;
    private final MobilityRepository mobilityRepository;

    public MobilityService(MobilityQuizConfig quizConfig, MobilityRepository mobilityRepository) {
        this.quizConfig = quizConfig;
        this.mobilityRepository = mobilityRepository;
    }

    public List<MobilityQuestion> getQuestions() {
        return quizConfig.getQuestions().stream()
                .map(q -> new MobilityQuestion()
                        .id(q.getId())
                        .category(q.getCategory())
                        .question(q.getText())
                        .options(q.getOptions()))
                .collect(Collectors.toList());
    }

    public void calculateQuizStatus(Map<String, QuizAnswer> requestBody, DogEntity dog) {

        // delete if entry exists to avoid duplicates (might be extended further)
        mobilityRepository.deleteByDog_Id(dog.getId());

        Map<MobilityCategory, List<QuizAnswer>> answersByCategory = quizConfig.getQuestions().stream()
                .collect(Collectors.groupingBy(
                        MobilityQuizConfig.Question::getCategory,
                        Collectors.mapping(q -> requestBody.get(q.getId()), Collectors.toList())
                ));

        answersByCategory.forEach((category, answers) -> {
            int score = answers.stream()
                    .mapToInt(QuizScoreCalculationService::mapAnswerToScore)
                    .sum();

            QuizCategoryStatus status = QuizScoreCalculationService.mapScoreToStatus(score);

            MobilityEntity entity = new MobilityEntity();
            entity.setDog(dog);
            entity.setType(category);
            entity.setStatus(status);
            entity.setCreatedTs(LocalDateTime.now());

            mobilityRepository.save(entity);
        });
    }

    public MobilityStatusResponse getMobilityStatusResponse(DogEntity dog) {
        List<MobilityEntity> mobilityEntities = mobilityRepository.findByDog_Id(dog.getId());

        MobilityStatusResponse response = new MobilityStatusResponse();

        for (MobilityEntity entity : mobilityEntities) {
            switch (entity.getType()) {
                case LUXATION -> response.setPatellarLuxation(entity.getStatus());
                case DYSPLASIA -> response.setHipDysplasia(entity.getStatus());
                case ARTHRITIS -> response.setArthritis(entity.getStatus());
            }
        }

        return response;
    }


}

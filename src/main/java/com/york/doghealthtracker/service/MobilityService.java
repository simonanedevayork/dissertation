package com.york.doghealthtracker.service;

import com.york.doghealthtracker.config.HormoneQuizConfig;
import com.york.doghealthtracker.config.MobilityQuizConfig;
import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.entity.MobilityEntity;
import com.york.doghealthtracker.model.*;
import com.york.doghealthtracker.repository.MobilityRepository;
import com.york.doghealthtracker.service.utils.QuizScoreCalculationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for dog mobility status management.
 *
 * @PreAuthorize method annotations validate that user has authorization to access the given resource.
 */
@Service
@Log4j2
public class MobilityService {

    private final MobilityQuizConfig quizConfig;
    private final MobilityRepository mobilityRepository;

    public MobilityService(MobilityQuizConfig quizConfig, MobilityRepository mobilityRepository) {
        this.quizConfig = quizConfig;
        this.mobilityRepository = mobilityRepository;
    }

    /**
     * Retrieves a list of questions related to the mobility quiz functionality.
     *
     * @return a list of MobilityQuestion
     */
    public List<MobilityQuestion> getQuestions() {
        return quizConfig.getQuestions().stream()
                .map(q -> new MobilityQuestion()
                        .id(q.getId())
                        .category(q.getCategory())
                        .question(q.getText())
                        .options(q.getOptions()))
                .collect(Collectors.toList());
    }

    /**
     * Processes quiz question submission. Calculates the results of the quiz answers, and saves them in the database.
     * Utilizes QuizScoreCalculationService for result calculations.
     * Deletes mobility quiz status, if such exists.
     *
     * @param dog         The dog entity to save quiz score for.
     * @param requestBody The quiz answers to process, calculate status for, and save in database.
     */
    public void calculateQuizStatus(DogEntity dog, Map<String, QuizAnswer> requestBody) {

        deleteHormoneQuizStatusIfExists(dog.getId());

        getGroupedAnswersByCategory(requestBody)
                .forEach((category, answers) -> {

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

    /**
     * Checks if hormone entries for given dog exist in database, and deletes them if they exist.
     *
     * @param dogId The dog to delete existing hormonal entries for.
     */
    private void deleteHormoneQuizStatusIfExists(String dogId) {
        if (mobilityRepository.findByDog_Id(dogId).isEmpty()) {
            log.info("No mobility entries in database to delete for dog with id: {}", dogId);
        } else {
            log.info("Deleting existing mobility entries in database for dog with id: {}", dogId);
            mobilityRepository.deleteByDog_Id(dogId);
        }
    }

    /**
     * Retrieves all questions from the quizConfig, and groups the respective answers of that category.
     *
     * @param requestBody The quiz answers to group.
     * @return a Map of MobilityCategory mapped to a list of answers for that category.
     */
    private Map<MobilityCategory, List<QuizAnswer>> getGroupedAnswersByCategory(Map<String, QuizAnswer> requestBody) {
        return quizConfig.getQuestions().stream()
                .collect(Collectors.groupingBy(
                        MobilityQuizConfig.Question::getCategory,
                        Collectors.mapping(q -> requestBody.get(q.getId()), Collectors.toList())
                ));
    }

    /**
     * Retrieves all MobilityEntity objects from the database related to the given dog, and maps them to a
     * MobilityStatusResponse object.
     *
     * @param dogId The dog id to return MobilityStatusResponse for.
     * @return a MobilityStatusResponse with mobility statuses.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId)")
    public MobilityStatusResponse getMobilityStatusResponse(String dogId) {
        List<MobilityEntity> mobilityEntities = mobilityRepository.findByDog_Id(dogId);

        MobilityStatusResponse response = new MobilityStatusResponse();

        for (MobilityEntity entity : mobilityEntities) {
            switch (entity.getType()) {
                case LUXATION -> response.setPatellarLuxation(entity.getStatus());
                case DYSPLASIA -> response.setHipDysplasia(entity.getStatus());
                case ARTHRITIS -> response.setArthritis(entity.getStatus());
            }
        }

        //TODO: calculate the highlights
        response.setHealthHighlights(Collections.emptyList());

        return response;
    }


}

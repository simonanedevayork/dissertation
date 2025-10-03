package com.york.doghealthtracker.service;

import com.york.doghealthtracker.config.HormoneQuizConfig;
import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.entity.HormoneEntity;
import com.york.doghealthtracker.model.*;
import com.york.doghealthtracker.repository.HormoneRepository;
import com.york.doghealthtracker.service.utils.QuizScoreCalculationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for dog hormone status management.
 *
 * @PreAuthorize method annotations validate that user has authorization to access the given resource.
 */
@Service
@Log4j2
public class HormoneService {

    private final HormoneQuizConfig quizConfig;
    private final HormoneRepository hormoneRepository;

    public HormoneService(HormoneQuizConfig quizConfig, HormoneRepository hormoneRepository) {
        this.quizConfig = quizConfig;
        this.hormoneRepository = hormoneRepository;
    }

    /**
     * Retrieves a list of questions related to the hormone quiz functionality.
     *
     * @return a list of HormoneQuestion.
     */
    public List<HormoneQuestion> getQuestions() {
        return quizConfig.getQuestions().stream()
                .map(q -> new HormoneQuestion()
                        .id(q.getId())
                        .category(q.getCategory())
                        .question(q.getText())
                        .options(q.getOptions()))
                .collect(Collectors.toList());
    }

    /**
     * Processes quiz question submission. Calculates the results of the quiz answers, and saves them in the database.
     * Utilizes QuizScoreCalculationService for result calculations.
     * Deletes hormone quiz status, if such exists.
     *
     * @param dog The dog entity to save quiz score for.
     * @param requestBody The quiz answers to process, calculate status for, and save in database.
     */
    public void calculateQuizScore(DogEntity dog, Map<String, QuizAnswer> requestBody) {

        deleteHormoneQuizStatusIfExists(dog.getId());

        getGroupedAnswersByCategory(requestBody)
                .forEach((category, answers) -> {

                    int score = answers.stream()
                            .mapToInt(QuizScoreCalculationService::mapAnswerToScore)
                            .sum();

                    QuizCategoryStatus status = QuizScoreCalculationService.mapScoreToStatus(score);

                    HormoneEntity entity = new HormoneEntity();
                    entity.setDog(dog);
                    entity.setType(category);
                    entity.setStatus(status);
                    entity.setCreatedTs(LocalDateTime.now());

                    hormoneRepository.save(entity);
                });
    }

    /**
     * Checks if hormone entries for given dog exist in database, and deletes them if they exist.
     *
     * @param dogId The dog to delete existing hormonal entries for.
     */
    private void deleteHormoneQuizStatusIfExists(String dogId) {
        if (hormoneRepository.findByDog_Id(dogId).isEmpty()) {
            log.info("No hormone entries in database to delete for dog with id: {}", dogId);
        } else {
            log.info("Deleting existing hormone entries in database for dog with id: {}", dogId);
            hormoneRepository.deleteByDog_Id(dogId);
        }
    }

    /**
     * Retrieves all questions from the quizConfig, and groups the respective answers of that category.
     *
     * @param requestBody The quiz answers to group.
     * @return a Map of HormoneCategory mapped to a list of answers for that category.
     */
    private Map<HormoneCategory, List<QuizAnswer>> getGroupedAnswersByCategory(Map<String, QuizAnswer> requestBody) {
        return quizConfig.getQuestions().stream()
                .collect(Collectors.groupingBy(
                        HormoneQuizConfig.Question::getCategory,
                        Collectors.mapping(q -> requestBody.get(q.getId()), Collectors.toList())
                ));
    }

    /**
     * Retrieves all HormoneEntity objects from the database related to the given dog, and maps them to a
     * HormoneStatusResponse object.
     *
     * @param dogId The dog id to return HormoneStatusResponse for.
     * @return a HormoneStatusResponse with hormone statuses.
     */
    @PreAuthorize("@authorizationService.hasDogOwnership(#dogId)")
    public HormoneStatusResponse getHormoneStatusResponse(String dogId) {
        List<HormoneEntity> hormoneEntities = hormoneRepository.findByDog_Id(dogId);

        HormoneStatusResponse response = new HormoneStatusResponse();

        for (HormoneEntity entity : hormoneEntities) {
            switch (entity.getType()) {
                case THYROID -> response.setThyroid(entity.getStatus());
                case ADRENAL -> response.setAdrenal(entity.getStatus());
                case PANCREATIC -> response.setPancreatic(entity.getStatus());
            }
        }

        return response;
    }

}

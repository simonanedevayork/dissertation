package com.york.doghealthtracker.service;

import com.york.doghealthtracker.config.HormoneQuizConfig;
import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.entity.HormoneEntity;
import com.york.doghealthtracker.entity.UserEntity;
import com.york.doghealthtracker.model.*;
import com.york.doghealthtracker.repository.DogRepository;
import com.york.doghealthtracker.repository.HormoneRepository;
import com.york.doghealthtracker.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.york.doghealthtracker.model.QuizAnswer.*;

@Service
public class HormonesService {

    private final HormoneQuizConfig quizConfig;
    private final HormoneRepository hormoneRepository;

    public HormonesService(HormoneQuizConfig quizConfig, HormoneRepository hormoneRepository) {
        this.quizConfig = quizConfig;
        this.hormoneRepository = hormoneRepository;
    }

    public List<HormoneQuestion> getQuestions() {
        return quizConfig.getQuestions().stream()
                .map(q -> new HormoneQuestion()
                        .id(q.getId())
                        .category(q.getCategory())
                        .question(q.getText())
                        .options(q.getOptions()))
                .collect(Collectors.toList());
    }

    public void calculateQuizStatus(Map<String, QuizAnswer> requestBody, DogEntity dog) {

        // delete if entry exists to avoid duplicates AT THIS POINT
        hormoneRepository.deleteByDog_Id(dog.getId());

        Map<HormoneCategory, List<QuizAnswer>> answersByCategory = quizConfig.getQuestions().stream()
                .collect(Collectors.groupingBy(
                        HormoneQuizConfig.Question::getCategory,
                        Collectors.mapping(q -> requestBody.get(q.getId()), Collectors.toList())
                ));

        answersByCategory.forEach((category, answers) -> {
            int score = answers.stream()
                    .mapToInt(this::mapAnswerToScore)
                    .sum();

            HormoneStatus status = mapScoreToStatus(score);

            HormoneEntity entity = new HormoneEntity();
            entity.setDog(dog);
            entity.setType(category);
            entity.setStatus(status);
            entity.setCreatedTs(LocalDateTime.now());

            hormoneRepository.save(entity);
        });
    }

    private int mapAnswerToScore(QuizAnswer answer) {
        return switch (answer) {
            case ALWAYS -> 3;
            case FREQUENTLY -> 2;
            case OCCASIONALLY -> 1;
            default -> 0;
        };
    }

    private HormoneStatus mapScoreToStatus(int score) {
        if (score <= 2) return HormoneStatus.GREEN;
        if (score <= 5) return HormoneStatus.YELLOW;
        return HormoneStatus.RED;
    }

    public HormoneStatusResponse getHormoneStatusResponse(DogEntity dog) {
        List<HormoneEntity> hormoneEntities = hormoneRepository.findByDog_Id(dog.getId());

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

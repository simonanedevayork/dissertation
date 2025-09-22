package com.york.doghealthtracker.service;

import com.york.doghealthtracker.config.HormoneQuizConfig;
import com.york.doghealthtracker.model.HormoneQuestion;
import com.york.doghealthtracker.model.HormoneStatusResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HormonesService {

    private final HormoneQuizConfig quizConfig;

    public HormonesService(HormoneQuizConfig quizConfig) {
        this.quizConfig = quizConfig;
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

    public void calculateQuizStatus(Map<String, String> requestBody) {
        //TODO: implement

        // get the answers, do the logic to calculate the status, and SAVE the status ONLY
        // no need to save actual answers of questions
    }

    public HormoneStatusResponse getHormoneStatusResponse() {

        // get the data from the database and return it

        return null;
    }

}

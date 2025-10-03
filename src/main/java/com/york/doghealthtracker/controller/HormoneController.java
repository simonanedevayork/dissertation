package com.york.doghealthtracker.controller;

import com.york.doghealthtracker.api.HormonesApi;
import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.model.HormoneQuestion;
import com.york.doghealthtracker.model.HormoneStatusResponse;
import com.york.doghealthtracker.model.QuizAnswer;
import com.york.doghealthtracker.service.HormoneService;
import com.york.doghealthtracker.service.security.UserContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class HormoneController implements HormonesApi {

    private final UserContextService userContextService;
    private final HormoneService hormonesService;

    public HormoneController(UserContextService userContextService, HormoneService hormonesService) {
        this.userContextService = userContextService;
        this.hormonesService = hormonesService;
    }

    @Override
    public ResponseEntity<List<HormoneQuestion>> getHormoneQuiz() {
        List<HormoneQuestion> questions = hormonesService.getQuestions();
        return ResponseEntity.ok(questions);
    }

    @Override
    public ResponseEntity<Void> submitHormoneQuiz(Map<String, QuizAnswer> requestBody) {

        DogEntity dog = userContextService.getDogInContext();

        hormonesService.calculateQuizScore(dog, requestBody);
        return ResponseEntity.status(201).build();
    }

    @Override
    public ResponseEntity<HormoneStatusResponse> getHormoneStatus() {

        DogEntity dog = userContextService.getDogInContext();

        HormoneStatusResponse status = hormonesService.getHormoneStatusResponse(dog.getId());
        return ResponseEntity.ok(status);
    }
}

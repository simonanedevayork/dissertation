package com.york.doghealthtracker.controller;

import com.york.doghealthtracker.api.HormonesApi;
import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.entity.UserEntity;
import com.york.doghealthtracker.model.HormoneQuestion;
import com.york.doghealthtracker.model.HormoneStatusResponse;
import com.york.doghealthtracker.model.QuizAnswer;
import com.york.doghealthtracker.service.HormonesService;
import com.york.doghealthtracker.service.utils.UserContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class HormonesController implements HormonesApi {

    private final UserContextService userContextService;
    private final HormonesService hormonesService;

    public HormonesController(UserContextService userContextService, HormonesService hormonesService) {
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

        hormonesService.calculateQuizStatus(requestBody, dog);
        return ResponseEntity.status(201).build();
    }

    @Override
    public ResponseEntity<HormoneStatusResponse> getHormoneStatus() {

        DogEntity dog = userContextService.getDogInContext();

        HormoneStatusResponse status = hormonesService.getHormoneStatusResponse(dog);
        return ResponseEntity.ok(status);
    }
}

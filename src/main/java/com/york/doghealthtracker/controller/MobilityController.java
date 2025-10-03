package com.york.doghealthtracker.controller;

import com.york.doghealthtracker.api.MobilityApi;
import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.model.*;
import com.york.doghealthtracker.service.MobilityService;
import com.york.doghealthtracker.service.security.UserContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class MobilityController implements MobilityApi {

    private final UserContextService userContextService;
    private final MobilityService mobilityService;

    public MobilityController(UserContextService userContextService, MobilityService mobilityService) {
        this.userContextService = userContextService;
        this.mobilityService = mobilityService;
    }

    @Override
    public ResponseEntity<List<MobilityQuestion>> getMobilityQuiz() {
        List<MobilityQuestion> questions = mobilityService.getQuestions();
        return ResponseEntity.ok(questions);
    }

    @Override
    public ResponseEntity<Void> submitMobilityQuiz(Map<String, QuizAnswer> requestBody) {
        DogEntity dog = userContextService.getDogInContext();

        mobilityService.calculateQuizStatus(dog, requestBody);
        return ResponseEntity.status(201).build();
    }

    @Override
    public ResponseEntity<MobilityStatusResponse> getMobilityStatus() {
        DogEntity dog = userContextService.getDogInContext();

        MobilityStatusResponse status = mobilityService.getMobilityStatusResponse(dog.getId());
        return ResponseEntity.ok(status);
    }

}

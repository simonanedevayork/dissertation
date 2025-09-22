package com.york.doghealthtracker.controller;

import com.york.doghealthtracker.api.HormonesApi;
import com.york.doghealthtracker.config.HormoneQuizConfig;
import com.york.doghealthtracker.model.HormoneQuestion;
import com.york.doghealthtracker.model.HormoneStatusResponse;
import com.york.doghealthtracker.service.HormonesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class HormonesController implements HormonesApi {

    private final HormonesService hormonesService;

    public HormonesController(HormonesService hormonesService) {
        this.hormonesService = hormonesService;
    }

    @Override
    public ResponseEntity<List<HormoneQuestion>> getHormoneQuiz() {
        List<HormoneQuestion> questions = hormonesService.getQuestions();
        return ResponseEntity.ok(questions);
    }

    @Override
    public ResponseEntity<Void> submitHormoneQuiz(Map<String, String> requestBody) {
        hormonesService.calculateQuizStatus(requestBody);
        return ResponseEntity.status(201).build();
    }

    @Override
    public ResponseEntity<HormoneStatusResponse> getHormoneStatus() {
        HormoneStatusResponse status = hormonesService.getHormoneStatusResponse();
        return ResponseEntity.ok(status);
    }
}

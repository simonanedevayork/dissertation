package com.york.doghealthtracker.controller;

import com.york.doghealthtracker.api.UsersApi;
import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.model.*;
import com.york.doghealthtracker.service.DogService;
import com.york.doghealthtracker.service.UserService;
import com.york.doghealthtracker.service.security.UserContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UsersApi {

    private final UserService userService;
    private final DogService dogService;
    private final UserContextService userContextService;

    public UserController(UserService userService, DogService dogService, UserContextService userContextService) {
        this.userService = userService;
        this.dogService = dogService;
        this.userContextService = userContextService;
    }

    @Override
    public ResponseEntity<UserResponse> getUserById(String participantId) {
        return userService.findUserById(participantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<UserResponse> updateUserById(String participantId, UserUpdateRequest req) {
        return userService.updateUser(participantId, req)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteUserById(String participantId) {
        return userService.deleteUser(participantId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<DogResponse> getDogByParticipantId(String participantId) {
            return dogService.findDogByOwnerId(participantId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

    @Override
    public ResponseEntity<DashboardResponse> getDashboardByParticipantId(String participantId) {
        DogEntity dog = userContextService.getDogInContext();
        DashboardResponse response = userService.getUserDashboard(participantId, dog.getId());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<OnboardingUpdateResponse> updateOnboardingAndConsent(String participantId, OnboardingUpdateRequest onboardingUpdateRequest) {
        return ResponseEntity.ok(userService.updateOnboardingAndConsent(participantId, onboardingUpdateRequest));
    }
}
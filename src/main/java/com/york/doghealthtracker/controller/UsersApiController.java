package com.york.doghealthtracker.controller;

import com.york.doghealthtracker.api.UsersApi;
import com.york.doghealthtracker.model.UserResponse;
import com.york.doghealthtracker.model.UserUpdateRequest;
import com.york.doghealthtracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersApiController implements UsersApi {

    private final UserService userService;

    public UsersApiController(UserService userService) {
        this.userService = userService;
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
}
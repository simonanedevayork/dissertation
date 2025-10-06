package com.york.doghealthtracker.controller;

import com.york.doghealthtracker.entity.UserEntity;
import com.york.doghealthtracker.security.payload.ForgotPasswordRequest;
import com.york.doghealthtracker.security.payload.JwtResponse;
import com.york.doghealthtracker.security.payload.LoginRequest;
import com.york.doghealthtracker.security.payload.ResetPasswordRequest;
import com.york.doghealthtracker.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller.
 * Implemented manually rather than through OpenAPI interface generation, due to the following reasons:
 * - authentication endpoints require close integration with Spring Security’s internal mechanisms
 * - authentication endpoints handle sensitive authentication logic which is not directly tied to the business entities
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.login(loginRequest);
        log.info("Successfully logged in user: {}", loginRequest.getEmail());
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody UserEntity user) {
        authService.register(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.sendPasswordResetEmail(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

}
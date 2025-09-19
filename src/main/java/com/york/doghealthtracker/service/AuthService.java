package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.UserEntity;
import com.york.doghealthtracker.repository.UserRepository;
import com.york.doghealthtracker.security.JwtUtils;
import com.york.doghealthtracker.security.payload.JwtResponse;
import com.york.doghealthtracker.security.payload.LoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Extract and return participantId in JWT response
        User userDetails = (User) authentication.getPrincipal();
        Optional<UserEntity> userEntity = userRepository.findByEmail(userDetails.getUsername());
        String participantId = userEntity.map(UserEntity::getId).orElse(null);

        return new JwtResponse(jwt, authentication.getName(), roles, participantId);
    }

    public String register(UserEntity user) {
        // Check if user already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with email: " + user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return "User Registered Successfully";
    }
}

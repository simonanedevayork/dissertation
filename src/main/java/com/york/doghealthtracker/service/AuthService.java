package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.UserEntity;
import com.york.doghealthtracker.exception.AccessDeniedException;
import com.york.doghealthtracker.repository.UserRepository;
import com.york.doghealthtracker.security.JwtUtils;
import com.york.doghealthtracker.security.payload.JwtResponse;
import com.york.doghealthtracker.security.payload.LoginRequest;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Service responsible for the authentication and authorization logic for the application.
 * Authentication is accomplished through validating user credentials against user data in the database; generating,
 * refreshing and validating JWT tokens; and creating new user accounts.
 */
@Service
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Logs in the user against provided user email and password. The authentication process is performed through the
     * following steps:
     * 1. Authentication token is generated, with the user email and password data;
     * 2. Authentication object is generated;
     * 3. Authentication object is stored in the SecurityContext, which allows other parts of the code to access the
     * currently authenticated user via SecurityContextHolder.getContext().getAuthentication();
     * 4. Jwt token is generated;
     * 5. User roles are collected;
     * 6. User is extracted from the Authentication object, and user entity is retrieved from database, in order to provide
     * user's participant id into the JwtResponse.
     *
     * @param loginRequest The login object containing the user email and password used in the authentication process.
     * @return JwtResponse object containing the jwt token, authentication type, username, user roles and participantId.
     */
    public JwtResponse login(LoginRequest loginRequest) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        User userDetails = (User) authentication.getPrincipal();
        Optional<UserEntity> userEntity = userRepository.findByEmail(userDetails.getUsername());
        String participantId = userEntity.map(UserEntity::getId).orElse(null);

        return new JwtResponse(jwt, authentication.getName(), roles, participantId);
    }

    /**
     * Registers a new user in the system. Validates that the provided user email is unique. Encodes the user password
     * to store it in the database safely.
     *
     * @param user The User entity containing the registration data.
     * @throws AccessDeniedException if user with such email already exists.
     */
    public void register(UserEntity user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.error("User already exists with email: {}", user.getEmail());
            throw new AccessDeniedException("User already exists with email: {}" + user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

}

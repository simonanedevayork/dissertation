package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.UserEntity;
import com.york.doghealthtracker.model.*;
import com.york.doghealthtracker.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * Service responsible for user management.
 */
@Service
@Log4j2
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDashboardService userDashboardService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserDashboardService userDashboardService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDashboardService = userDashboardService;
    }

    /**
     * Retrieves a given user by user id.
     *
     * @param userId The id of the user to retrieve.
     * @return Optional of UserResponse, or empty optional.
     */
    public Optional<UserResponse> findUserById(String userId) {
        return userRepository.findById(userId)
                .map(entity -> new UserResponse(entity.getId(), entity.getEmail(), entity.getOnboardingCompleted(), entity.getConsentGranted()));
    }

    /**
     * Updates a given user in the database.
     *
     * @param userId        The id of the user to update.
     * @param updateRequest The UserUpdateRequest containing the information to update.
     * @return Optional of UserResponse, or empty optional.
     */
    public Optional<UserResponse> updateUser(String userId, UserUpdateRequest updateRequest) {
        return userRepository.findById(userId).map(entity -> {
            entity.setEmail(updateRequest.getEmail());
            entity.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            userRepository.save(entity);
            return new UserResponse(entity.getId(), entity.getEmail(), entity.getOnboardingCompleted(), entity.getConsentGranted());
        });
    }

    /**
     * Updates a given user from the database.
     *
     * @param userId The id of the user to delete.
     * @return true if user is deleted successfully, false otherwise.
     */
    public boolean deleteUser(String userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    /**
     * Retrieves user dashboard for given user.
     *
     * @param userId The id of the user to retrieve dashboard for.
     * @param dogId  The id of the dog related to the user.
     * @return DashboardResponse object containing the user dashboard data.
     */
    public DashboardResponse getUserDashboard(String userId, String dogId) {
        log.info("Requesting dashboard information for user with id: {}", userId);
        return userDashboardService.getDashboard(userId, dogId);
    }

    public OnboardingUpdateResponse updateOnboardingAndConsent(String participantId, OnboardingUpdateRequest onboardingUpdateRequest) {
        return userRepository.findById(participantId)
                .map(user -> {
                    user.setOnboardingCompleted(onboardingUpdateRequest.getOnboardingCompleted());
                    user.setConsentGranted(onboardingUpdateRequest.getConsentGranted());
                    user.setConsentTimestamp(Instant.now());
                    userRepository.save(user);

                    log.info("User {} updated onboardingCompleted={} and consentGranted={}",
                            participantId, user.getOnboardingCompleted(), user.getConsentGranted());

                    return new OnboardingUpdateResponse(
                            user.getId(),
                            user.getOnboardingCompleted(),
                            user.getConsentGranted(),
                            OffsetDateTime.ofInstant(user.getConsentTimestamp(), ZoneOffset.UTC)
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + participantId));
    }

    /**
     * Loads UserDetails by username, which is represented by the user email.
     *
     * @param email The email of the user.
     * @return UserDetails object containing the user data.
     * @throws UsernameNotFoundException if user is not found by the given email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();
    }
}
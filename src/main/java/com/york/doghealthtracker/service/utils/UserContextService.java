package com.york.doghealthtracker.service.utils;

import com.york.doghealthtracker.entity.DogEntity;
import com.york.doghealthtracker.entity.UserEntity;
import com.york.doghealthtracker.repository.DogRepository;
import com.york.doghealthtracker.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Extracts the user in context and the dog related to that user.
 */
@Service
public class UserContextService {
    private final UserRepository userRepository;
    private final DogRepository dogRepository;

    public UserContextService(UserRepository userRepository, DogRepository dogRepository) {
        this.userRepository = userRepository;
        this.dogRepository = dogRepository;
    }

    public UserEntity getUserInContext() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public DogEntity getDogInContext() {
        UserEntity user = getUserInContext();
        return dogRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new RuntimeException("Dog not found"));
    }
}

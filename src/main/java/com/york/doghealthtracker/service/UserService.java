package com.york.doghealthtracker.service;

import com.york.doghealthtracker.entity.UserEntity;
import com.york.doghealthtracker.model.UserResponse;
import com.york.doghealthtracker.model.UserUpdateRequest;
import com.york.doghealthtracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserResponse> findUserById(String participantId) {
        return userRepository.findById(participantId)
                .map(entity -> new UserResponse(entity.getId(), entity.getEmail()));
    }

    public Optional<UserResponse> updateUser(String participantId, UserUpdateRequest req) {
        return userRepository.findById(participantId).map(entity -> {
            entity.setEmail(req.getEmail());
            entity.setPassword(passwordEncoder.encode(req.getPassword()));
            userRepository.save(entity);
            return new UserResponse(entity.getId(), entity.getEmail());
        });
    }

    public boolean deleteUser(String participantId) {
        if (userRepository.existsById(participantId)) {
            userRepository.deleteById(participantId);
            return true;
        }
        return false;
    }

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

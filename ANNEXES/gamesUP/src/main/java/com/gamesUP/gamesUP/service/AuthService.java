package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.UserDTO
        ;
import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.enumeration.Role;
import com.gamesUP.gamesUP.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserDTO userDTO) {
        if (userDTO
                .getPassword() == null || userDTO
                .getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }

        if (userRepository.existsByEmail(userDTO
                .getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setName(userDTO
                .getName());
        user.setEmail(userDTO
                .getEmail());
        user.setPasswordHash(passwordEncoder.encode(userDTO
                .getPassword()));
        user.setRole(Role.CUSTOMER);
        return userRepository.save(user);
    }

}

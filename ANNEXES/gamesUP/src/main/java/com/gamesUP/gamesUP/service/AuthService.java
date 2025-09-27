package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.UserDTO
        ;
import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.enumeration.Role;
import com.gamesUP.gamesUP.exception.ResourceNotFoundException;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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

    @Transactional(readOnly = true)
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("This user does not exist with the email: " + email));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        return jwtUtil.generateToken(user.getName(), user.getEmail(), user.getRole().name());
    }

}

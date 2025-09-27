package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.AuthDTO;
import com.gamesUP.gamesUP.dto.UserDTO;
import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private  final AuthService authService;

    @RequestMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserDTO userDTO) {
        User createdUser = authService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @RequestMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AuthDTO authDTO) {
        String token = authService.login(authDTO.getEmail(), authDTO.getPassword());
        return ResponseEntity.ok(token);
    }
}

package com.gamesUP.gamesUP.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.UserDTO;
import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        userRepository.deleteAll();
    }

    @Test
    void testRegister() throws Exception {
        // Préparation des données
        UserDTO userDTO = UserDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        // Envoi de la requête POST
        String url = "http://localhost:" + port + "/api/auth/register";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = objectMapper.writeValueAsString(userDTO);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        User response = restTemplate.postForObject(url, request, User.class);

        // Vérifications
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getEmail()).isEqualTo(userDTO.getEmail());
        assertThat(userRepository.existsByEmail(userDTO.getEmail())).isTrue();
    }
}
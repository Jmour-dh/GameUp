package com.gamesUP.gamesUP.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.RecommendationDTO;
import com.gamesUP.gamesUP.dto.UserDataDTO;
import com.gamesUP.gamesUP.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecommendationService recommendationService;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testGetRecommendations_returnsList() throws Exception {
        RecommendationDTO dto = new RecommendationDTO(1L, "Game A", 4.5);
        when(recommendationService.getRecommendations(any(UserDataDTO.class))).thenReturn(List.of(dto));

        UserDataDTO request = new UserDataDTO(1L, List.of());

        mockMvc.perform(post("/api/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].game_id").value(dto.getGame_id()))
                .andExpect(jsonPath("$[0].game_name").value(dto.getGame_name()))
                .andExpect(jsonPath("$[0].rating").value(dto.getRating()));
    }
}

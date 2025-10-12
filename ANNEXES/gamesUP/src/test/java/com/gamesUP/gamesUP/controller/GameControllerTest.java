package com.gamesUP.gamesUP.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.GameDTO;
import com.gamesUP.gamesUP.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameService gameService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllGames() throws Exception {
        GameDTO dto = new GameDTO(1L, "Game A", "Author A", "Action", 1L, 1L, 1);

        when(gameService.getAll()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/games")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(dto.getName()))
                .andExpect(jsonPath("$[0].author").value(dto.getAuthor()));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testGetGameById() throws Exception {
        GameDTO dto = new GameDTO(1L, "Game A", "Author A", "Action", 1L, 1L, 1);

        when(gameService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/games/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.author").value(dto.getAuthor()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateGame() throws Exception {
        GameDTO request = new GameDTO(null, "Game A", "Author A", "Action", 1L, 1L, 1);
        GameDTO saved = new GameDTO(1L, "Game A", "Author A", "Action", 1L, 1L, 1);

        when(gameService.create(any(GameDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value(saved.getName()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateGame() throws Exception {
        GameDTO request = new GameDTO(null, "Updated Game", "Author A", "RPG", 1L, 1L, 2);
        GameDTO updated = new GameDTO(1L, "Updated Game", "Author A", "RPG", 1L, 1L, 2);

        when(gameService.update(1L, request)).thenReturn(updated);

        mockMvc.perform(put("/api/games/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updated.getName()))
                .andExpect(jsonPath("$.genre").value(updated.getGenre()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteGame() throws Exception {
        mockMvc.perform(delete("/api/games/1"))
                .andExpect(status().isNoContent());
    }
}

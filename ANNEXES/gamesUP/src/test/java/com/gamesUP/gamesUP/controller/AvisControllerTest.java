package com.gamesUP.gamesUP.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.AvisDTO;
import com.gamesUP.gamesUP.service.AvisService;
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
class AvisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AvisService avisService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllAvis() throws Exception {
        AvisDTO dto = new AvisDTO(1L, "Super avis", 5, 1L);
        when(avisService.findAll()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/avis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value(dto.getComment()))
                .andExpect(jsonPath("$[0].note").value(dto.getNote()));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testGetAvisById() throws Exception {
        AvisDTO dto = new AvisDTO(1L, "Super avis", 5, 1L);
        when(avisService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/avis/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value(dto.getComment()))
                .andExpect(jsonPath("$.note").value(dto.getNote()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateAvis() throws Exception {
        AvisDTO request = new AvisDTO(null, "Nouveau", 4, 1L);
        AvisDTO saved = new AvisDTO(1L, "Nouveau", 4, 1L);

        when(avisService.create(any(AvisDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/avis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.comment").value(saved.getComment()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateAvis() throws Exception {
        AvisDTO request = new AvisDTO(null, "Modifié", 3, 2L);
        AvisDTO updated = new AvisDTO(1L, "Modifié", 3, 2L);

        when(avisService.update(1L, request)).thenReturn(updated);

        mockMvc.perform(put("/api/avis/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value(updated.getComment()))
                .andExpect(jsonPath("$.note").value(updated.getNote()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteAvis() throws Exception {
        mockMvc.perform(delete("/api/avis/1"))
                .andExpect(status().isNoContent());
    }
}

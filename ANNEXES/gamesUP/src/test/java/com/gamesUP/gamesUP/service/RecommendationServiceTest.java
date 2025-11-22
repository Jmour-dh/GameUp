package com.gamesUP.gamesUP.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.RecommendationDTO;
import com.gamesUP.gamesUP.dto.UserDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationServiceTest {

    private RecommendationService recommendationService;
    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService();
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(recommendationService, "restTemplate");
        mockServer = MockRestServiceServer.createServer(restTemplate);
        ReflectionTestUtils.setField(recommendationService, "pythonUrl", "http://localhost:8000/recommendations");
    }

    @Test
    void testGetRecommendations_success() throws Exception {
        String json = mapper.writeValueAsString(
                java.util.Map.of("recommendations", List.of(
                        java.util.Map.of("game_id", 1, "game_name", "Game A", "rating", 4.5)
                ))
        );

        mockServer.expect(MockRestRequestMatchers.requestTo("http://localhost:8000/recommendations"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess(json, MediaType.APPLICATION_JSON));

        UserDataDTO userData = new UserDataDTO(1L, List.of());
        List<RecommendationDTO> recs = recommendationService.getRecommendations(userData);

        assertNotNull(recs);
        assertEquals(1, recs.size());
        RecommendationDTO dto = recs.get(0);
        assertEquals(1L, dto.getGame_id());
        assertEquals("Game A", dto.getGame_name());
        assertEquals(4.5, dto.getRating());
        mockServer.verify();
    }

}

package com.gamesUP.gamesUP.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationDTO {
    @JsonProperty("game_id")
    private Long game_id;
    @JsonProperty("game_name")
    private String game_name;
    @JsonProperty("rating")
    private Double rating;
}

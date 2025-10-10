package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvisDTO {

    private Long id;

    @NotBlank(message = "Comment is required")
    private String comment;

    @Min(value = 1, message = "Note must be at least 1")
    @Max(value = 5, message = "Note must be at most 5")
    private int note;

    @NotNull(message = "Game ID is required")
    private Long gameId;
}
package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "Genre is required")
    private String genre;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Publisher ID is required")
    private Long publisherId;

    @Min(value = 1, message = "Edition number must be at least 1")
    private int numEdition;
}
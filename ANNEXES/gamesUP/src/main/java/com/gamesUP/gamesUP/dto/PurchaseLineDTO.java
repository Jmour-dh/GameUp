package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseLineDTO {

    private Long id;

    @Min(value = 1, message = "User ID must be at least 1")
    private int userId;

    @NotNull(message = "Game ID is required")
    private Long gameId;

    @Min(value = 0, message = "Price must be at least 0")
    private double price;

    @NotNull(message = "Purchase ID is required")
    private Long purchaseId;
}
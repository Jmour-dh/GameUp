package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseDTO {

    private Long id;

    @NotNull(message = "Line IDs cannot be null")
    private List<Long> lineIds;

    @NotNull(message = "Date is required")
    private Date date;

    private boolean paid;
    private boolean delivered;
    private boolean archived;
}
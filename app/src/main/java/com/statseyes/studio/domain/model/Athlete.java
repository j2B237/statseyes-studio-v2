package com.statseyes.studio.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Athlete(
        Integer id,
        String firstname,
        String lastname,
        LocalDate birthday,
        char gender,
        String imageUrl,
        Integer accountId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer positionId,
        String positionName,
        Integer teamId,
        String teamName,
        Double height,
        Double weight,
        Double maxSpeed
) {}
package com.statseyes.studio.domain.model;

import java.time.Instant;
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
        Instant createdAt,
        Instant updatedAt,
        Integer positionId,
        String positionName,
        Integer teamId,
        String teamName,
        Double height,
        Double weight,
        Double maxSpeed
) {}
package com.statseyes.studio.domain.model;

import java.time.LocalDate;

public record NewAthlete(
        String firstname,
        String lastname,
        LocalDate birthday,
        char gender,
        Integer teamId,
        Integer positionId,
        Double heightCm,
        Double weightKg,
        Integer jerseyNumber,
        String nationalityCode
) {
}

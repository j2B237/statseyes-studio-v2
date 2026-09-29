package com.statseyes.studio.domain.model;

import com.statseyes.studio.infrastructure.persistence.entity.AccountEntity;
import com.statseyes.studio.infrastructure.persistence.entity.PositionEntity;
import com.statseyes.studio.infrastructure.persistence.entity.TeamEntity;

import java.time.Instant;
import java.time.LocalDateTime;

public record Athlete(
    Integer id,
    String firstname,
    String lastname,
    LocalDateTime birthday,
    Character gender,
    String imageUrl,
    AccountEntity account,
    Instant created_at,
    Instant updated_at,
    PositionEntity position,
    TeamEntity team,
    Double height,
    Double weight,
    Double maxSpeed
) {
}

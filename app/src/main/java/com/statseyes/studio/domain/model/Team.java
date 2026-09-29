package com.statseyes.studio.domain.model;

import com.statseyes.studio.infrastructure.persistence.entity.AccountEntity;

import java.time.Instant;

public record Team(
        Integer id,
        String name,
        String abbreviation,
        String sport,
        AccountEntity account,
        Instant created_at,
        Instant updated_at
) {
}

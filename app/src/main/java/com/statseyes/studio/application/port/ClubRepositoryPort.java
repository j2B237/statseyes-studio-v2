package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.ClubSummary;

import java.util.Optional;

public interface ClubRepositoryPort {
    Optional<ClubSummary> findByAccountId(Integer accountId);
}

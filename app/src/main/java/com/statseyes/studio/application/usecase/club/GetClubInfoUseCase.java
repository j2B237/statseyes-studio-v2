package com.statseyes.studio.application.usecase.club;

import com.statseyes.studio.domain.model.ClubSummary;
import com.statseyes.studio.application.port.ClubRepositoryPort;
import com.statseyes.studio.infrastructure.persistence.entity.ClubEntity;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetClubInfoUseCase {

    // ===================
    // INSTANCE VARIABLES
    // ===================

    private final ClubRepositoryPort repositoryPort;

    // ====================
    // PUBLIC API
    // ===================

    public GetClubInfoUseCase(ClubRepositoryPort repository){
        this.repositoryPort = repository;
    }

    public Optional<String> execute(Integer accountId){
        return repositoryPort.findByAccountId(accountId)
                .map(ClubSummary::name);
    }
}

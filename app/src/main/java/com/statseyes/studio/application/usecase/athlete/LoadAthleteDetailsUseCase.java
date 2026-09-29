package com.statseyes.studio.application.usecase.athlete;

import com.statseyes.studio.application.port.AthleteRepositoryPort;
import com.statseyes.studio.domain.model.AuthenticatedUser;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import org.springframework.stereotype.Component;

@Component
public class LoadAthleteDetailsUseCase {

    private final AthleteRepositoryPort repository;
    private final SessionAdapter sessionService;

    public LoadAthleteDetailsUseCase(
            AthleteRepositoryPort repository,
            SessionAdapter sessionService
    ){
        this.repository = repository;
        this.sessionService = sessionService;
    }

    public Athlete execute(Integer athleteId){
        AuthenticatedUser user = sessionService.getCurrentUser();
        if(user == null){
            throw new IllegalStateException("user not found");
        }
        return repository.findById(athleteId, user.id());
    }
}

package com.statseyes.studio.application.usecase.athlete;

import com.statseyes.studio.application.port.AthleteRepositoryPort;
import com.statseyes.studio.application.port.ImageResolvePort;
import com.statseyes.studio.domain.config.Resource_Type;
import com.statseyes.studio.domain.exception.DomainValidationException;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.domain.model.AuthenticatedUser;
import com.statseyes.studio.infrastructure.security.SessionAdapter;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetAthleteImageUseCase {

    // ====================
    // INSTANCE VARIABLES
    // ====================

    // ====================
    // SPRING PORT
    // ====================

    private final AthleteRepositoryPort repository;
    private final ImageResolvePort imageResolve;
    private final SessionAdapter session;


    // ================
    // PUBLIC API
    // ================

    public GetAthleteImageUseCase(
            AthleteRepositoryPort repository,
            ImageResolvePort imageResolve,
            SessionAdapter session
    ){
        this.repository = repository;
        this.imageResolve = imageResolve;
        this.session = session;
    }

    public Optional<String> execute(Integer athleteId){

        AuthenticatedUser account = session.getCurrentUser();
        if (account == null) {
            throw new DomainValidationException("utilisateur inconnu.");
        }

        Athlete a = repository.findById(athleteId, account.id());
        return imageResolve.resolveUrl(a.imageUrl(), Resource_Type.ATHLETE);
    }
}

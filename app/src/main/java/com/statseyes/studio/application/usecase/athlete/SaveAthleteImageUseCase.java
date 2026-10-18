package com.statseyes.studio.application.usecase.athlete;

import com.statseyes.studio.application.port.AthleteRepositoryPort;
import com.statseyes.studio.domain.exception.DomainValidationException;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.domain.model.AuthenticatedUser;

import com.statseyes.studio.application.port.ImageResolvePort;


import com.statseyes.studio.domain.service.ImageService;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Component
public class SaveAthleteImageUseCase {

    // ==============
    // SPRING BEAN
    // ==============

    private final SessionAdapter session;
    private final ImageService imageService;

    // =============
    // PUBLIC API
    // =============

    public SaveAthleteImageUseCase(
            ImageService imageService,
            SessionAdapter session
    ){
        this.imageService = imageService;
        this.session = session;
    }

    public String execute( Path filename ) throws IOException {

        AuthenticatedUser account = session.getCurrentUser();
        if(account == null) {
            throw new DomainValidationException("Utilisateur inconnu.");
        }

        return imageService.saveAthleteImage(filename, account.id());
    }
}

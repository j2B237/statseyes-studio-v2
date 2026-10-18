package com.statseyes.studio.application.usecase.club;

import com.statseyes.studio.domain.config.Resource_Type;
import com.statseyes.studio.domain.model.ClubSummary;
import com.statseyes.studio.application.port.ClubRepositoryPort;
import com.statseyes.studio.application.port.ImageResolvePort;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetClubLogoUseCase {

    // ====================
    // INSTANCE VARIABLES
    // ====================

    private final ClubRepositoryPort clubRepositoryPort;
    private final ImageResolvePort imageResolvePort;

    // ===================
    // PUBLIC API
    // ==================

    public GetClubLogoUseCase(
            ClubRepositoryPort clubRepositoryPort,
            ImageResolvePort imageResolvePort
    ){
        this.clubRepositoryPort = clubRepositoryPort;
        this.imageResolvePort = imageResolvePort;
    }

    public Optional<String> execute(Integer accountId){
        return clubRepositoryPort.findByAccountId(accountId)
                .map(ClubSummary::logoFilename)
                .flatMap(f -> imageResolvePort.resolveUrl(f, Resource_Type.CLUB));
    }
}

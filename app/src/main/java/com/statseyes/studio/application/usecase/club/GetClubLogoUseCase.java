package com.statseyes.studio.application.usecase.club;

import com.statseyes.studio.domain.model.ClubSummary;
import com.statseyes.studio.application.port.ClubRepositoryPort;
import com.statseyes.studio.application.port.LogoResolvePort;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetClubLogoUseCase {

    // ====================
    // INSTANCE VARIABLES
    // ====================

    private final ClubRepositoryPort clubRepositoryPort;
    private final LogoResolvePort logoResolvePort;

    // ===================
    // PUBLIC API
    // ==================

    public GetClubLogoUseCase(
            ClubRepositoryPort clubRepositoryPort,
            LogoResolvePort logoResolvePort
    ){
        this.clubRepositoryPort = clubRepositoryPort;
        this.logoResolvePort = logoResolvePort;
    }

    public Optional<String> execute(Integer accountId){
        return clubRepositoryPort.findByAccountId(accountId)
                .map(ClubSummary::logoFilename)
                .flatMap(logoResolvePort::resolveUrl);
    }
}

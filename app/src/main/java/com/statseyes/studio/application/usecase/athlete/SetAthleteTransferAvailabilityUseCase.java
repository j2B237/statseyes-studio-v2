package com.statseyes.studio.application.usecase.athlete;

import com.statseyes.studio.application.port.AthleteRepositoryPort;
import com.statseyes.studio.domain.model.Athlete;

import org.springframework.stereotype.Component;

@Component
public class SetAthleteTransferAvailabilityUseCase {
    // ===================
    // INSTANCE VARIABLES
    // ===================

    private final AthleteRepositoryPort port;

    public SetAthleteTransferAvailabilityUseCase(AthleteRepositoryPort port){
        this.port = port;
    }

    public Athlete execute(Integer athleteId, Integer accountId, boolean available){
        return port.updateTransferAvailability(athleteId, accountId, available);
    }
}

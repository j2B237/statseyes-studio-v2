package com.statseyes.studio.application.usecase.athlete;

import com.statseyes.studio.application.port.AthleteRepositoryPort;

import com.statseyes.studio.domain.model.Athlete;
import org.springframework.stereotype.Component;

@Component
public class LoadAthleteDetailsUseCase {

    private final AthleteRepositoryPort repository;

    public LoadAthleteDetailsUseCase(AthleteRepositoryPort repository){
        this.repository = repository;
    }

    public Athlete execute(Integer athleteId){
        return repository.findById(athleteId);
    }
}

package com.statseyes.studio.application.usecase.athlete;

import com.statseyes.studio.domain.model.AthleteFormOptions;
import com.statseyes.studio.application.port.AthleteFormOptionsPort;

import org.springframework.stereotype.Component;

@Component
public class LoadAthleteFormOptionsUseCase {

    private final AthleteFormOptionsPort repository;
    public LoadAthleteFormOptionsUseCase(AthleteFormOptionsPort repository){
        this.repository = repository;
    }

    public AthleteFormOptions execute(Integer accountId){
        return repository.load(accountId);
    }
}


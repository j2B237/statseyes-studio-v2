package com.statseyes.studio.application.usecase.session;

import com.statseyes.studio.application.port.ImportedSessionRepositoryPort;
import com.statseyes.studio.domain.model.ImportedSession;

import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class GetLatestImportedSessionUseCase {

    private final ImportedSessionRepositoryPort repository;

    public GetLatestImportedSessionUseCase(
            ImportedSessionRepositoryPort repository
    ){
        this.repository = repository;
    }

    public ImportedSession execute(Integer athleteId){
        return repository.findByAthleteId(athleteId)
                .stream()
                .findFirst()
                .orElse(null);
    }
}

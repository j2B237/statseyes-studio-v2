package com.statseyes.studio.application.usecase.session;

import com.statseyes.studio.application.port.ImportedSessionRepositoryPort;
import com.statseyes.studio.domain.model.ImportedSession;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListSessionsForAthleteUseCase {

    // =====================
    // INSTANCE VARIABLES
    // =====================

    private final ImportedSessionRepositoryPort repository;

    // ================
    // PUBLIC API
    // ================

    public ListSessionsForAthleteUseCase(ImportedSessionRepositoryPort repository){
        this.repository = repository;
    }

    public List<ImportedSession> execute(Integer athleteId){
        return repository.findByAthleteId(athleteId);
    }
}

package com.statseyes.studio.application.usecase.session;

import com.statseyes.studio.domain.model.SessionComparison;
import com.statseyes.studio.domain.model.ImportedSession;
import com.statseyes.studio.domain.service.SessionComparator;

import org.springframework.stereotype.Component;

@Component
public class CompareSessionsUseCase {

    // ====================
    // INSTANCE VARIABLES
    // ====================

    private final SessionComparator sessionComparator = new SessionComparator();

    public SessionComparison execute(ImportedSession a, ImportedSession b){
        return sessionComparator.compare(a.metrics(), b.metrics());
    }
}

package com.statseyes.studio.infrastructure.persistence.adapter;

import com.statseyes.studio.application.port.AccountSummaryPort;
import com.statseyes.studio.domain.model.AccountSummary;
import com.statseyes.studio.infrastructure.persistence.repository.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JpaAccountSummaryAdapter implements AccountSummaryPort {


    private final AthleteJpaRepository athleteRepository;
    private final TeamJpaRepository teamRepository;
    private final ImportedPodSessionJpaRepository importedSessionRepository;


    public JpaAccountSummaryAdapter(
            AthleteJpaRepository athleteRepository,
            TeamJpaRepository teamRepository,
            ImportedPodSessionJpaRepository importedSessionRepository
    ){
        this.athleteRepository = athleteRepository;
        this.teamRepository = teamRepository;
        this.importedSessionRepository = importedSessionRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public AccountSummary load(Integer accountId){

    }

}

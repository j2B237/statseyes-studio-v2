package com.statseyes.studio.infrastructure.persistence.adapter;

import com.statseyes.studio.application.port.AthleteRepositoryPort;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.domain.model.AuthenticatedUser;
import com.statseyes.studio.infrastructure.persistence.entity.AccountEntity;
import com.statseyes.studio.infrastructure.persistence.entity.AthleteEntity;
import com.statseyes.studio.infrastructure.persistence.mapper.AthleteMapper;

import com.statseyes.studio.infrastructure.persistence.repository.AthleteJpaRepository;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class JpaAthleteRepositoryAdapter implements AthleteRepositoryPort {

    private final AthleteJpaRepository repository;
    private final AthleteMapper athleteMapper;
    private final SessionAdapter sessionService;

    public JpaAthleteRepositoryAdapter(
            AthleteJpaRepository repository,
            AthleteMapper athleteMapper,
            SessionAdapter sessionService
    ){
        this.repository = repository;
        this.athleteMapper = athleteMapper;
        this.sessionService = sessionService;
    }

    @Override
    @Transactional(readOnly = true)
    public Athlete findById(Integer athleteId){

        AuthenticatedUser user = requireCurrentAccount();
        Optional<AthleteEntity> result = repository.findByIdAndAccount_Id(
                athleteId,
                user.id()
        );

        return result.map(athleteMapper::toDomain).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Athlete> findAllByCurrentAccount(){
        AuthenticatedUser user = requireCurrentAccount();
        return repository.findAllByAccount_Id(user.id())
                .stream()
                .map(athleteMapper::toDomain)
                .toList();
    }

    // ================
    // PRIVATE API
    // ================

    private AuthenticatedUser requireCurrentAccount() {
        AuthenticatedUser account = sessionService.getCurrentUser();
        if (account == null || account.id() == null) {
            throw new SecurityException("No authenticated account");
        }
        return account;
    }

}

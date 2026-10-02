package com.statseyes.studio.infrastructure.persistence.adapter;

import com.statseyes.studio.application.port.ClubRepositoryPort;
import com.statseyes.studio.domain.model.ClubSummary;
import com.statseyes.studio.infrastructure.persistence.mapper.ClubMapper;
import com.statseyes.studio.infrastructure.persistence.repository.ClubRepository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.util.Optional;

@Component
public class JpaClubRepositoryAdapter implements ClubRepositoryPort{

    // =======================
    // INSTANCE REPOSITORY
    // ======================

    private final ClubRepository repository;
    private final ClubMapper clubMapper;

    // ======================
    // PUBLIC API
    // =====================

    public JpaClubRepositoryAdapter(
            ClubRepository repository,
            ClubMapper clubMapper
    ){
        this.repository = repository;
        this.clubMapper = clubMapper;
    }

    @Override
    @Cacheable(value = "club_summary", key = "'account:' + #accountId")
    @Transactional(readOnly = true)
    public Optional<ClubSummary> findByAccountId(Integer accountId){
        return repository.findByAccountId(accountId)
                .map(clubMapper::toDomain);
    }
}

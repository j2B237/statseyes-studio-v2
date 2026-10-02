package com.statseyes.studio.infrastructure.persistence.adapter;

import com.statseyes.studio.application.port.AthleteRepositoryPort;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.infrastructure.persistence.entity.AthleteEntity;
import com.statseyes.studio.infrastructure.persistence.mapper.AthleteMapper;
import com.statseyes.studio.infrastructure.persistence.repository.AthleteJpaRepository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Optional;

@Component
public class JpaAthleteRepositoryAdapter implements AthleteRepositoryPort {

    private final AthleteJpaRepository repository;
    private final AthleteMapper athleteMapper;

    public JpaAthleteRepositoryAdapter(AthleteJpaRepository repository, AthleteMapper athleteMapper) {
        this.repository = repository;
        this.athleteMapper = athleteMapper;
    }

    @Override
    @Cacheable(
        value = "athletes",
        key = "'account:' + #accountId + ':athlete:' + #athleteId"
    )
    @Transactional(readOnly = true)
    public Athlete findById(Integer athleteId, Integer accountId) {
        Optional<AthleteEntity> result = repository.findByIdAndAccount_Id(athleteId, accountId);
        return result.map(athleteMapper::toDomain).orElse(null);
    }

    @Override
    @Cacheable(value = "athletes", key = "'account:' + #accountId")
    @Transactional(readOnly = true)
    public List<Athlete> findAllByAccount(Integer accountId) {
        return repository.findAllByAccount_IdOrderByFirstnameAscWithTeamAndPosition(accountId)
                .stream()
                .map(athleteMapper::toDomain)
                .toList();
    }
}
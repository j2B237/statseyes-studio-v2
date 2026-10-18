package com.statseyes.studio.infrastructure.persistence.adapter;

import com.statseyes.studio.application.port.AthleteRepositoryPort;
import com.statseyes.studio.domain.exception.DomainValidationException;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.domain.model.NewAthlete;
import com.statseyes.studio.infrastructure.persistence.entity.AccountEntity;
import com.statseyes.studio.infrastructure.persistence.entity.AthleteEntity;
import com.statseyes.studio.infrastructure.persistence.entity.PositionEntity;
import com.statseyes.studio.infrastructure.persistence.entity.TeamEntity;
import com.statseyes.studio.infrastructure.persistence.mapper.AthleteMapper;
import com.statseyes.studio.infrastructure.persistence.repository.AthleteJpaRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import jakarta.persistence.EntityManager;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class JpaAthleteRepositoryAdapter implements AthleteRepositoryPort {

    private final EntityManager em;
    private final AthleteJpaRepository repository;
    private final AthleteMapper athleteMapper;

    public JpaAthleteRepositoryAdapter(
            EntityManager em,
            AthleteJpaRepository repository, AthleteMapper athleteMapper
    ) {
        this.em = em;
        this.repository = repository;
        this.athleteMapper = athleteMapper;
    }

    @Override
    @Transactional
    @CacheEvict(
            value = {"athletes", "account_summary"},
            allEntries = true
    )
    public Athlete create(NewAthlete n, Integer accountId){

        // Make sure an athlete with the same first and lastname does not exist
        if(repository.findByAccount_IdAndFirstnameIgnoreCaseAndLastnameIgnoreCase(
                accountId, n.firstname(), n.lastname()
        ).isPresent()){
            throw new DomainValidationException("Un athlete portant ce nom existe deja.");
        }

        // The team must belongs to the current account
        Long owned = em.createQuery(
                "select count(t) from TeamEntity t where t.id= :t and t.account.id = :a ",
                Long.class)
                .setParameter("t", n.teamId())
                .setParameter("a", accountId)
                .getSingleResult();

        if(owned == 0) throw new DomainValidationException("Équipe est invalide");

        AthleteEntity e = new AthleteEntity();

        e.setFirstname(n.firstname());
        e.setLastname(n.lastname());
        e.setBirthday(n.birthday());
        e.setGender(n.gender());
        e.setHeight(n.heightCm());
        e.setWeight(n.weightKg());
        e.setJerseyNumber(n.jerseyNumber());
        e.setNationalityCode(n.nationalityCode());
        e.setImageUrl(n.imageUrl());
        e.setActive(true);
        e.setAvailable(false);
        e.setAccount(em.getReference(AccountEntity.class, accountId));
        e.setTeam(em.getReference(TeamEntity.class, n.teamId()));
        e.setPosition(em.getReference(PositionEntity.class, n.positionId()));

        Instant now = Instant.now();
        e.setUpdatedAt(now);
        e.setCreatedAt(now);

        return athleteMapper.toDomain(
                repository.save(e)
        );
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

    @Override
    @Transactional
    @CacheEvict(value = "athletes", allEntries = true)
    public Athlete updateTransferAvailability(
            Integer athleteId, Integer accountId,
            boolean available
    ){
        AthleteEntity e = repository.findByIdAndAccount_Id(athleteId, accountId)
                .orElseThrow(() -> new DomainValidationException("Athlete introuvable"));

        e.setAvailable(available);
        e.setUpdatedAt(Instant.now());

        return athleteMapper.toDomain(e);
    }

    @Override
    @Modifying
    @Transactional
    @CacheEvict(value = "athletes", allEntries = true)
    public void updateImageURL(Integer athleteId, Integer accountId, String filename) {
        repository.updateImageURL(athleteId, accountId, filename);
    }
}
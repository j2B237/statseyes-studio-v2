package com.statseyes.studio.infrastructure.persistence.adapter;

import com.statseyes.studio.application.port.AthleteProfilePort;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.domain.model.Availability;
import com.statseyes.studio.domain.model.DominantFoot;
import com.statseyes.studio.infrastructure.persistence.entity.AthleteEntity;
import com.statseyes.studio.infrastructure.persistence.mapper.AthleteMapper;
import com.statseyes.studio.infrastructure.persistence.repository.AthleteJpaRepository;
import com.statseyes.studio.infrastructure.persistence.repository.PositionJpaRepository;

import org.springframework.stereotype.Component;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class JpaAthleteProfileAdapter implements AthleteProfilePort {

    //////////////////////////
    ///  CLASS VARIABLES  ///
    ////////////////////////

    private final AthleteJpaRepository athletes;
    private final PositionJpaRepository positions;
    private final AthleteMapper mapper;

    // Constructor
    public JpaAthleteProfileAdapter (
            AthleteJpaRepository athletes,
            PositionJpaRepository positions,
            AthleteMapper mapper
    ){
        this.athletes = athletes;
        this.positions = positions;
        this.mapper = mapper;
    }

    @Override
    @Cacheable(
            value = "athletes",
            key = "'account:' + #accountId + ':athlete:' + #athleteId"
    )
    @Transactional
    public Athlete updateProfile(Integer athleteId, Integer accountId, DominantFoot foot,
                          Integer mainPositionId, Set<Integer> secondaryPositionIds
    ){
        AthleteEntity e = athletes.findByIdAndAccount_Id(athleteId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Athlete introuvable"));

        e.setDominantFoot(foot);
        e.setPosition(positions.getReferenceById(mainPositionId));
        e.getSecondaryPositions().clear();
        e.getSecondaryPositions().addAll(positions.findAllById(secondaryPositionIds));
        return mapper.toDomainWithProfile(athletes.save(e));
    }

    @Override
    @Cacheable(
            value = "athletes",
            key = "'account:' + #accountId + ':athlete:' + #athleteId"
    )
    @Transactional
    public Athlete updateAvailability(Integer athleteId, Integer accountId, Availability a){

        AthleteEntity e = athletes.findByIdAndAccount_Id(athleteId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Athlète introuvable."));

        e.setAvailabilityStatus(a.status());
        e.setAvailabilityReason(a.reason());
        e.setAvailabilitySince(a.since());
        e.setAvailabilityExpectedReturn(a.expectedReturn());

        return mapper.toDomainWithProfile(athletes.save(e));
    }


}

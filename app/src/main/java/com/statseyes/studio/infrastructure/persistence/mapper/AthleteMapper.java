package com.statseyes.studio.infrastructure.persistence.mapper;

import com.statseyes.studio.domain.model.Availability;
import com.statseyes.studio.domain.model.PositionRef;
import com.statseyes.studio.infrastructure.persistence.entity.AthleteEntity;
import com.statseyes.studio.domain.model.Athlete;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class AthleteMapper {

    public Athlete toDomain(AthleteEntity entity) {
        return new Athlete(
                entity.getId(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getBirthday(),                 // LocalDate -> LocalDate, pas de conversion
                entity.getGender(),
                entity.getImageUrl(),
                entity.getAccount().getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getPosition() != null ? entity.getPosition().getId()   : null,
                entity.getPosition() != null ? entity.getPosition().getName() : null,
                entity.getTeam()     != null ? entity.getTeam().getId()       : null,
                entity.getTeam()     != null ? entity.getTeam().getName()     : null,
                entity.getHeight(),
                entity.getWeight(),
                entity.getMaxSpeed(),
                entity.getNationalityCode(),
                entity.getJerseyNumber(),
                entity.isActive(),
                entity.isAvailable(),
                entity.getDominantFoot(),
                availabilityOf(entity),
                List.of()
        );
    }

    public Athlete toDomainWithProfile(AthleteEntity e){
        List<PositionRef> secondaries = e.getSecondaryPositions()
                                        .stream()
                .map(p -> new PositionRef(p.getId(), p.getName()))
                .toList();
        return new Athlete(
                e.getId(),
                e.getFirstname(),
                e.getLastname(),
                e.getBirthday(),                 // LocalDate -> LocalDate, pas de conversion
                e.getGender(),
                e.getImageUrl(),
                e.getAccount().getId(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getPosition() != null ? e.getPosition().getId()   : null,
                e.getPosition() != null ? e.getPosition().getName() : null,
                e.getTeam()     != null ? e.getTeam().getId()       : null,
                e.getTeam()     != null ? e.getTeam().getName()     : null,
                e.getHeight(),
                e.getWeight(),
                e.getMaxSpeed(),
                e.getNationalityCode(),
                e.getJerseyNumber(),
                e.isActive(),
                e.isAvailable(),
                e.getDominantFoot(),
                availabilityOf(e),
                secondaries
        );
    }

    public Availability availabilityOf(AthleteEntity e){
        return new Availability(
                e.getAvailabilityStatus(), e.getAvailabilityReason(),
                e.getAvailabilitySince(),e.getAvailabilityExpectedReturn()
        );
    }
    private LocalDateTime toLocalDateTime(java.time.Instant instant) {
        return instant == null ? null : instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
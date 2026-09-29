package com.statseyes.studio.infrastructure.persistence.mapper;

import com.statseyes.studio.infrastructure.persistence.entity.AthleteEntity;
import com.statseyes.studio.domain.model.Athlete;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

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
                toLocalDateTime(entity.getCreatedAt()),
                toLocalDateTime(entity.getUpdatedAt()),
                entity.getPosition() != null ? entity.getPosition().getId()   : null,
                entity.getPosition() != null ? entity.getPosition().getName() : null,
                entity.getTeam()     != null ? entity.getTeam().getId()       : null,
                entity.getTeam()     != null ? entity.getTeam().getName()     : null,
                entity.getHeight(),
                entity.getWeight(),
                entity.getMaxSpeed()
        );
    }

    private LocalDateTime toLocalDateTime(java.time.Instant instant) {
        return instant == null ? null : instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
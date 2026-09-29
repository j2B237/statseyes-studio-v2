package com.statseyes.studio.infrastructure.persistence.mapper;

import com.statseyes.studio.infrastructure.persistence.entity.AthleteEntity;
import com.statseyes.studio.domain.model.Athlete;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AthleteMapper {

    public Athlete toDomain(AthleteEntity entity){
        return new Athlete(
                entity.getId(),
                entity.getFirstname(),
                entity.getLastname(),
                LocalDateTime.from(entity.getBirthday()),
                entity.getGender(),
                entity.getImageUrl(),
                entity.getAccount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getPosition(),
                entity.getTeam(),
                entity.getHeight(),
                entity.getWeight(),
                entity.getMaxSpeed()
        );
    }
}

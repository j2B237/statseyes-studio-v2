package com.statseyes.studio.infrastructure.persistence.mapper;

import com.statseyes.studio.domain.model.ClubSummary;
import com.statseyes.studio.infrastructure.persistence.entity.ClubEntity;

import org.springframework.stereotype.Component;
@Component
public class ClubMapper {

    public ClubSummary toDomain(ClubEntity entity){
        return new ClubSummary(
                entity.getId(),
                entity.getName(),
                entity.getLogo_url(),
                entity.getAccount().getId(),
                entity.isActive()
        );
    }
}

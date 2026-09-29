package com.statseyes.studio.infrastructure.persistence.mapper;

import com.statseyes.studio.domain.model.Team;
import com.statseyes.studio.infrastructure.persistence.entity.TeamEntity;

import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    public Team toDomain(TeamEntity entity){
        return new Team(
                entity.getId(),
                entity.getName(),
                entity.getAbbreviation(),
                entity.getSport(),
                entity.getAccount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

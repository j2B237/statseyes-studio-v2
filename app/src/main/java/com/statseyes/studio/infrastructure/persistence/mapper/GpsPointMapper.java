package com.statseyes.studio.infrastructure.persistence.mapper;

import com.statseyes.studio.domain.model.GpsPoint;
import com.statseyes.studio.infrastructure.persistence.entity.GpsPointEntity;

import org.springframework.stereotype.Component;

@Component
public class GpsPointMapper {

    public GpsPoint toDomain(GpsPointEntity entity){
        return new GpsPoint(
            entity.getLatitude(),
            entity.getLongitude()
        );
    }
}

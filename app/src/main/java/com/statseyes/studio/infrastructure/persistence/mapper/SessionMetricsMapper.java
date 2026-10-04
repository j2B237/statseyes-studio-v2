package com.statseyes.studio.infrastructure.persistence.mapper;

import com.statseyes.studio.domain.model.SessionMetrics;
import com.statseyes.studio.infrastructure.persistence.entity.ImportedPodSessionEntity;

import org.springframework.stereotype.Component;

@Component
public class SessionMetricsMapper {

    public SessionMetrics metricsOf(ImportedPodSessionEntity entity){
        return new SessionMetrics(
                entity.getTotalDistanceM(),
                entity.getMaxSpeedKmh(),
                entity.getAvgSpeedKmh(),
                entity.getSprintCount(),
                entity.getDominantCourseDeg(),
                0,
                0,
                0,
                0,
                0
        );
    }
}

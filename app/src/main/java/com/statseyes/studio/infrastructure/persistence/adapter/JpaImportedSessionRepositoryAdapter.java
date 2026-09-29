package com.statseyes.studio.infrastructure.persistence.adapter;

import com.statseyes.studio.application.port.ImportedSessionRepositoryPort;

import com.statseyes.studio.domain.model.ImportedSession;
import com.statseyes.studio.domain.model.PodSessionData;
import com.statseyes.studio.domain.model.SessionMetrics;
import com.statseyes.studio.infrastructure.persistence.mapper.ImportedSessionMapper;
import com.statseyes.studio.infrastructure.persistence.mapper.SessionMetricsMapper;
import com.statseyes.studio.infrastructure.persistence.entity.GpsPointEntity;
import com.statseyes.studio.infrastructure.persistence.entity.ImportedPodSessionEntity;
import com.statseyes.studio.infrastructure.persistence.repository.ImportedPodSessionJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class JpaImportedSessionRepositoryAdapter implements ImportedSessionRepositoryPort {

    private final ImportedPodSessionJpaRepository repository;
    private final ImportedSessionMapper sessionMapper;
    private final SessionMetricsMapper metricsMapper;

    public JpaImportedSessionRepositoryAdapter(
            ImportedPodSessionJpaRepository repository,
            ImportedSessionMapper sessionMapper,
            SessionMetricsMapper metricsMapper
    ){
        this.repository = repository;
        this.sessionMapper = sessionMapper;
        this.metricsMapper = metricsMapper;
    }

    @Override
    @Transactional
    public ImportedSession save(
            PodSessionData sessionData,
            SessionMetrics metrics,
            String sourceFileName,
            Integer accountId,
            Integer athleteId
    ){
        ImportedPodSessionEntity entity = ImportedPodSessionEntity.builder()
                .podSessionNumber(sessionData.sessionNumber())
                .sourceFileName(sourceFileName)
                .importedAt(LocalDateTime.now())
                .totalDistanceM(metrics.totalDistanceMeters())
                .maxSpeedKmh(metrics.maxSpeedKmh())
                .avgSpeedKmh(metrics.avgSpeedKmh())
                .sprintCount(metrics.sprintCount())
                .dominantCourseDeg(metrics.dominantCourseDegrees())
                .accountId(accountId)
                .athleteId(athleteId)
                .build();

        sessionData.samples().forEach(sample -> {
            GpsPointEntity point = GpsPointEntity.builder()
                    .importedSession(entity)
                    .timeMs(sample.Time_MS())
                    .latitude(sample.Latitude())
                    .longitude(sample.Longitude())
                    .speedCms(sample.Speed())
                    .courseDeciDeg(sample.Course())
                    .sane(sample.Valid())
                    .build();
            entity.getPoints().add(point);
        });

        return sessionMapper.toDomain(
                repository.save(entity),
                metrics
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImportedSession> findAll(){
        return repository.findAll().stream()
                .map(e ->
                    sessionMapper.toDomain(e, metricsMapper.metricsOf(e)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImportedSession> findByAthleteId(Integer athleteId){
        return repository.findByAthleteIdOrderByImportedAtDesc(athleteId)
                .stream()
                .map(e -> sessionMapper.toDomain(e, metricsMapper.metricsOf(e)))
                .toList();
    }



}

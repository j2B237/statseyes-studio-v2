package com.statseyes.studio.infrastructure.persistence.adapter;

import com.statseyes.studio.application.port.ImportedSessionRepositoryPort;

import com.statseyes.studio.domain.model.ImportedSession;
import com.statseyes.studio.domain.model.PodSessionData;
import com.statseyes.studio.domain.model.SessionMetrics;
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

    public JpaImportedSessionRepositoryAdapter(
            ImportedPodSessionJpaRepository repository
    ){
        this.repository = repository;
    }

    @Override
    public ImportedSession save(
            PodSessionData sessionData,
            SessionMetrics metrics,
            String sourceFileName,
            Integer accountId
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

        ImportedPodSessionEntity saved = repository.save(entity);

        return new ImportedSession(
                saved.getId(),
                saved.getPodSessionNumber(),
                saved.getSourceFileName(),
                saved.getImportedAt(),
                metrics
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImportedSession> findAll(){
        return repository.findAll().stream()
                .map(e ->
                    new ImportedSession(
                            e.getId(),
                            e.getPodSessionNumber(),
                            e.getSourceFileName(),
                            e.getImportedAt(),
                            new SessionMetrics(
                                    e.getTotalDistanceM(),
                                    e.getMaxSpeedKmh(),
                                    e.getAvgSpeedKmh(),
                                    e.getSprintCount(),
                                    e.getDominantCourseDeg()
                            )
                    )
                ).toList();
    }

}

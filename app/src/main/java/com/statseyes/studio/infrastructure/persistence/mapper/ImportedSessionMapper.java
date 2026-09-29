package com.statseyes.studio.infrastructure.persistence.mapper;

import com.statseyes.studio.domain.model.ImportedSession;
import com.statseyes.studio.domain.model.SessionMetrics;
import com.statseyes.studio.infrastructure.persistence.entity.ImportedPodSessionEntity;

import org.springframework.stereotype.Component;

@Component
public class ImportedSessionMapper {

    public ImportedSession toDomain(ImportedPodSessionEntity entity, SessionMetrics metrics){
        return new ImportedSession(
                entity.getId(),
                entity.getPodSessionNumber(),
                entity.getSourceFileName(),
                entity.getImportedAt(),
                metrics
        );
    }
}


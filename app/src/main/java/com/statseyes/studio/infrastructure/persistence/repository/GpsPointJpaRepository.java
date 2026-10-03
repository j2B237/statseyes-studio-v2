package com.statseyes.studio.infrastructure.persistence.repository;

import com.statseyes.studio.domain.model.GpsPoint;
import com.statseyes.studio.infrastructure.persistence.entity.GpsPointEntity;
import com.statseyes.studio.infrastructure.persistence.entity.ImportedPodSessionEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GpsPointJpaRepository extends JpaRepository<GpsPointEntity, Integer>{

    List<GpsPointEntity> findAllByImportedSession_Id(Integer importedSessionId);
    Optional<GpsPointEntity> findByIdAndImportedSession_Id(Integer gpsPointId, Integer importedSessionId);
}

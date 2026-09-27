package com.statseyes.studio.infrastructure.persistence.repository;

import com.statseyes.studio.infrastructure.persistence.entity.ImportedPodSessionEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImportedPodSessionJpaRepository extends JpaRepository<ImportedPodSessionEntity, Integer>{

    ImportedPodSessionEntity save(ImportedPodSessionEntity entity);
    List<ImportedPodSessionEntity> findAll();

}

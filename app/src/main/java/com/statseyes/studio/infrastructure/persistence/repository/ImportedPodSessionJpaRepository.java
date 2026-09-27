package com.statseyes.studio.infrastructure.persistence.repository;

import com.statseyes.studio.infrastructure.persistence.entity.ImportedPodSessionEntity;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImportedPodSessionJpaRepository extends JpaRepository<ImportedPodSessionEntity, Integer>{

    ImportedPodSessionEntity save(@NonNull ImportedPodSessionEntity entity);
    List<ImportedPodSessionEntity> findAll();

}

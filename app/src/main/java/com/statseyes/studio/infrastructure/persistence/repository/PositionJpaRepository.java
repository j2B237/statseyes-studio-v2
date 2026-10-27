package com.statseyes.studio.infrastructure.persistence.repository;

import com.statseyes.studio.infrastructure.persistence.entity.PositionEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionJpaRepository extends JpaRepository<PositionEntity, Integer>{
    Optional<PositionEntity> findByCodeIgnoreCaseAndSportIgnoreCase(
        String code,
        String sport
    );
    List<PositionEntity> findAllBySportOrderByNameAsc(String sportName);
}
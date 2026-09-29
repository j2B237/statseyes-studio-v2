package com.statseyes.studio.infrastructure.persistence.repository;

import com.statseyes.studio.infrastructure.persistence.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamJpaRepository extends JpaRepository<TeamEntity, Integer>{

    List<TeamEntity> findAllByAccount_IdOrderByNameAsc(Integer accountId);
    Optional<TeamEntity> findByIdAndAccount_Id(Integer teamId, Integer accountId);
    long countByAccount_Id(Integer teamId);
}



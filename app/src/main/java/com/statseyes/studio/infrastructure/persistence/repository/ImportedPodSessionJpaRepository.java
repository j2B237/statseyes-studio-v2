package com.statseyes.studio.infrastructure.persistence.repository;

import com.statseyes.studio.infrastructure.persistence.entity.ImportedPodSessionEntity;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ImportedPodSessionJpaRepository extends JpaRepository<ImportedPodSessionEntity, Integer>{

    List<ImportedPodSessionEntity> findByAthleteIdOrderByIdDesc(Integer athleteId);
    List<ImportedPodSessionEntity> findByAthleteIdOrderByImportedAtDesc(Integer athleteId);
    long countByAccountId(Integer accountId);

    @Query(""" 
            SELECT MAX(s.importedAt)
            FROM ImportedPodSessionEntity s WHERE s.accountId = :accountId
    """)
    LocalDateTime findLastImportedAt(@Param("accountId") Integer accountId);

    @Query("""
                SELECT COALESCE(AVG(s.totalDistanceM), 0) as avgDistance,
                       COALESCE(AVG(s.maxSpeedKmh), 0) as avgMaxSpeed,
                       COALESCE(AVG(s.sprintCount), 0) as avgSprintCount
                FROM ImportedPodSessionEntity s
                JOIN AthleteEntity a ON a.id = s.athleteId
                WHERE a.team.id = :teamId
            """
    )
    TeamAverageProjection computeTeamAverages(@Param("teamId") Integer teamId);
}

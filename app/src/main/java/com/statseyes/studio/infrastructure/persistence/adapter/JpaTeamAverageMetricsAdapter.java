package com.statseyes.studio.infrastructure.persistence.adapter;

import com.statseyes.studio.application.port.TeamAverageMetricsPort;
import com.statseyes.studio.domain.model.TeamAverageMetrics;
import com.statseyes.studio.infrastructure.persistence.repository.ImportedPodSessionJpaRepository;

import com.statseyes.studio.infrastructure.persistence.repository.TeamAverageProjection;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JpaTeamAverageMetricsAdapter implements TeamAverageMetricsPort{

    // ===================
    // INSTANCE VARIABLES
    // ===================

    private final ImportedPodSessionJpaRepository repository;


    // ===========
    // PUBLIC API
    // ===========

    public JpaTeamAverageMetricsAdapter(ImportedPodSessionJpaRepository repository){
        this.repository = repository;
    }


    @Override
    @Cacheable(
            value = "team_avg_metrics",
            key = "'teamId:' + #teamId"
    )
    @Transactional(readOnly = true)
    public TeamAverageMetrics computeFromTeam(Integer teamId){

        if (teamId == null)return new TeamAverageMetrics(0,0,0);

        TeamAverageProjection p = repository.computeTeamAverages(teamId);

        if(p ==null)return new TeamAverageMetrics(0,0, 0);

        return new TeamAverageMetrics(
                p.getAvgDistance(),
                p.getAvgMaxSpeed(),
                p.getAvgSprintCount()
        );
    }
}

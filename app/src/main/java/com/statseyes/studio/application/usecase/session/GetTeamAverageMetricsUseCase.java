package com.statseyes.studio.application.usecase.session;

import com.statseyes.studio.application.port.TeamAverageMetricsPort;
import com.statseyes.studio.domain.model.TeamAverageMetrics;

import org.springframework.stereotype.Component;

@Component
public class GetTeamAverageMetricsUseCase {

    // ======================
    // INSTANCE VARIABLES
    // =====================

    private final TeamAverageMetricsPort port;

    // =====================
    // PUBLIC API
    // =====================

    public GetTeamAverageMetricsUseCase(TeamAverageMetricsPort port){
        this.port = port;
    }

    public TeamAverageMetrics execute(Integer teamId){
        return port.computeFromTeam(teamId);
    }

}

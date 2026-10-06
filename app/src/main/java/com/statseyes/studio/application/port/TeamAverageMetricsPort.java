package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.TeamAverageMetrics;

public interface TeamAverageMetricsPort {
    TeamAverageMetrics computeFromTeam(Integer teamId);
}

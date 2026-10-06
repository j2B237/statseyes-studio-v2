package com.statseyes.studio.domain.model;

public record TeamAverageMetrics(
        double avgDistanceM,
        double avgMaxSpeedKmh,
        double avgSprintCount
) {
}

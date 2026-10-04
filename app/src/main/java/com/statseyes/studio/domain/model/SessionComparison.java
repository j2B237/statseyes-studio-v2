package com.statseyes.studio.domain.model;

public record SessionComparison(
        SessionMetrics sessionA,
        SessionMetrics sessionB,
        double distanceDeltaM,
        double maxSpeedDeltaKmh,
        double avgSpeedDeltaKmh,
        int sprintCountDelta
) {
}

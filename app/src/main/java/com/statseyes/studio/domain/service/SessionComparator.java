package com.statseyes.studio.domain.service;

import com.statseyes.studio.domain.model.SessionMetrics;
import com.statseyes.studio.domain.model.SessionComparison;


public class SessionComparator {

    public SessionComparison compare(SessionMetrics a, SessionMetrics b){
        return new SessionComparison(
                a, b,
                b.totalDistanceMeters() - a.totalDistanceMeters(),
                b.maxSpeedKmh() - a.maxSpeedKmh(),
                b.avgSpeedKmh() - a.avgSpeedKmh(),
                b.sprintCount() - a.sprintCount()
        );
    }

}

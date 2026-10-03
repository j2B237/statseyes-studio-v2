package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.GpsPoint;
import com.statseyes.studio.domain.model.ImportedSession;

import java.util.List;

public interface SessionGpsPointsPort {
    List<GpsPoint> findBySessionId(Integer importedSessionId);

}

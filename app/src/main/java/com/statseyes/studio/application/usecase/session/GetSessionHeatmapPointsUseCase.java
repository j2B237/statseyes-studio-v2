package com.statseyes.studio.application.usecase.session;

import com.statseyes.studio.domain.model.GpsPoint;
import com.statseyes.studio.application.port.SessionGpsPointsPort;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetSessionHeatmapPointsUseCase {

    private final SessionGpsPointsPort port;

    public GetSessionHeatmapPointsUseCase(SessionGpsPointsPort port){
        this.port = port;
    }

    public List<GpsPoint> execute(Integer importedSessionId){
        return port.findBySessionId(importedSessionId);
    }
}

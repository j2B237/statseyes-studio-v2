package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.*;
import java.util.List;

public interface ImportSessionRepositoryPort {
    ImportedSession save(
            PodSessionData sessionData,
            SessionMetrics metrics,
            String sourceFileName
    );

    List<ImportedSession> findAll();
}

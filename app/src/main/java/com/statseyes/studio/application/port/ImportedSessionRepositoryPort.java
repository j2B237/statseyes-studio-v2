package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.*;
import java.util.List;

public interface ImportedSessionRepositoryPort {
    ImportedSession save(
            PodSessionData sessionData,
            SessionMetrics metrics,
            String sourceFileName,
            Integer accountId
    );

    List<ImportedSession> findAll();
}

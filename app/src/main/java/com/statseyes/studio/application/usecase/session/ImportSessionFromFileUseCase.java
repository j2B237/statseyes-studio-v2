package com.statseyes.studio.application.usecase.session;


import com.statseyes.studio.application.port.ImportedSessionRepositoryPort;
import com.statseyes.studio.application.port.PodFileImportPort;
import com.statseyes.studio.domain.exception.PodFileFormatException;
import com.statseyes.studio.domain.model.ImportedSession;
import com.statseyes.studio.domain.model.PodSessionData;
import com.statseyes.studio.domain.model.SessionMetrics;
import com.statseyes.studio.domain.service.SessionMetricsCalculator;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

@Component
public class ImportSessionFromFileUseCase {

    private final PodFileImportPort podFileImportPort;
    private final ImportedSessionRepositoryPort importedSessionRepository;
    private final SessionMetricsCalculator metricsCalculator =
            new SessionMetricsCalculator();

    public ImportSessionFromFileUseCase(
            PodFileImportPort podFileImportPort,
            ImportedSessionRepositoryPort importedSessionRepository
    ){
        this.podFileImportPort = podFileImportPort;
        this.importedSessionRepository = importedSessionRepository;

    }

    public List<ImportedSession> execute(
            InputStream binaryStream,
            String sourceLabel,
            Integer accountId,
            Integer athleteId
    ){
        List<PodSessionData> sessions = listSessionsInFile(binaryStream);

        List<PodSessionData> meaningFulSessions = sessions.stream()
                .filter(s -> s.samples().size() >= 2)  // sous 2 points, aucune metrique calculable
                .toList();

        if(meaningFulSessions.isEmpty()){
            throw new PodFileFormatException("Aucune session trouvée dans ce flux.");
        }

        return meaningFulSessions.stream()
                .map(session -> {
                    SessionMetrics metrics = computeMetrics(session);
                    return importedSessionRepository.save(session, metrics, sourceLabel, accountId, athleteId);
                })
                .toList();
    }

    public List<PodSessionData> listSessionsInFile(InputStream binaryStream) {
        return podFileImportPort.parse(binaryStream);
    }

    public SessionMetrics computeMetrics(PodSessionData session) {
        return metricsCalculator.compute(session.samples());
    }
}

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

    public ImportedSession execute(
            InputStream binaryStream,
            String sourceLabel,
            Integer accountId,
            Integer athleteId
    ){
        PodSessionData last = podFileImportPort.parseLastSession(binaryStream)
                .orElseThrow(() -> new PodFileFormatException("Aucune session exploitable"));

        SessionMetrics metrics = metricsCalculator.compute(last.samples());
        return importedSessionRepository.save(last, metrics, sourceLabel, accountId, athleteId);

    }

    public List<PodSessionData> listSessionsInFile(InputStream binaryStream) {
        return podFileImportPort.parse(binaryStream);
    }

    public SessionMetrics computeMetrics(PodSessionData session) {
        return metricsCalculator.compute(session.samples());
    }
}

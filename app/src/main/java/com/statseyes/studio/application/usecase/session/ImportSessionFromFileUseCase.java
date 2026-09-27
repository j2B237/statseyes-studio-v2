package com.statseyes.studio.application.usecase.session;


import com.statseyes.studio.application.port.ImportedSessionRepositoryPort;
import com.statseyes.studio.application.port.PodFileImportPort;
import com.statseyes.studio.domain.exception.PodFileFormatException;
import com.statseyes.studio.domain.model.ImportedSession;
import com.statseyes.studio.domain.model.PodSessionData;
import com.statseyes.studio.domain.model.SessionMetrics;
import com.statseyes.studio.domain.service.SessionMetricsCalculator;

import org.springframework.stereotype.Component;

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

    public List<ImportedSession> execute(Path binaryFilePath){
        List<PodSessionData> sessions = listSessionsInFile(binaryFilePath);

        if(sessions.isEmpty()){
            throw new PodFileFormatException("Aucune session trouvee dans ce fichier.");
        }

        String fileName = binaryFilePath.getFileName().toString();
        return sessions.stream()
                .map(session -> {
                    SessionMetrics metrics = computeMetrics(session);
                    return importedSessionRepository.save(session, metrics, fileName);
                })
                .toList();
    }

    public List<PodSessionData> listSessionsInFile(Path binaryFilePath) {
        return podFileImportPort.parse(binaryFilePath);
    }

    public SessionMetrics computeMetrics(PodSessionData session) {
        return metricsCalculator.compute(session.samples());
    }
}

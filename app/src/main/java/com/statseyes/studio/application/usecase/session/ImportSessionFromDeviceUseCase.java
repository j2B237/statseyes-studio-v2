package com.statseyes.studio.application.usecase.session;

import com.statseyes.studio.application.port.PodDevicePort;
import com.statseyes.studio.domain.model.ImportedSession;
import com.statseyes.studio.domain.model.PodDump;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class ImportSessionFromDeviceUseCase {

    // ===================
    // INSTANCE VARIABLES
    // ===================

    private final PodDevicePort devicePort;
    private final ImportSessionFromFileUseCase importFromStream;   // le cas d'usage « dernière session » déjà en place

    public ImportSessionFromDeviceUseCase(
        PodDevicePort devicePort,
        ImportSessionFromFileUseCase importFromStream
    ) {
        this.devicePort = devicePort;
        this.importFromStream = importFromStream;
    }

    public ImportedSession execute(
        String deviceId,
        Integer accountId,
        Integer athleteId,
        ProgressListener progress
    ) throws IOException {
        try (
            PodDump dump = devicePort.openDump(deviceId)
        ) {
            InputStream in = new ProgressInputStream(dump.stream(), dump.totalBytes(), progress);
            return importFromStream.execute(in, "USB " + deviceId, accountId, athleteId);
        }
    }
}

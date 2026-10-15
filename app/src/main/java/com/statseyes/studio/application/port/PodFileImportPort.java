package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.PodSessionData;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface PodFileImportPort {
    List<PodSessionData> parse(InputStream binaryStream);
    Optional<PodSessionData> parseLastSession(InputStream binaryStream);
}

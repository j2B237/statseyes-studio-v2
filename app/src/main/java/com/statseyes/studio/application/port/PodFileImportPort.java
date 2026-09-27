package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.PodSessionData;

import java.nio.file.Path;
import java.util.List;

public interface PodFileImportPort {
    List<PodSessionData> parse(Path binaryFilePath);
}

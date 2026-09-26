package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.GpsData;

import java.nio.file.Path;
import java.util.List;

public interface PodFileImportPort {
    List<GpsData> parse(Path binaryFilePath);
}

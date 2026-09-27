package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.PodSessionData;
import java.io.InputStream;
import java.util.List;

public interface PodFileImportPort {
    List<PodSessionData> parse(InputStream binaryStream);
}

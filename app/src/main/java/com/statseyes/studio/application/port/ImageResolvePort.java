package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.config.Resource_Type;

import java.util.Optional;

public interface ImageResolvePort {

    Optional<String> resolveUrl(String filename, Resource_Type dir);
}

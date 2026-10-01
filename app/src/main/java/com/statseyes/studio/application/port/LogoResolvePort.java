package com.statseyes.studio.application.port;

import java.util.Optional;

public interface LogoResolvePort {

    Optional<String> resolveUrl(String filename);
}

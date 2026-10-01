package com.statseyes.studio.infrastructure.persistence.adapter;

import com.statseyes.studio.application.port.LogoResolvePort;
import com.statseyes.studio.domain.service.LogoService;

import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class LogoServiceAdapter implements LogoResolvePort {

    // =================
    // INSTANCE VARIABLE
    // =================

    private final LogoService logoService;

    // =================
    // PUBLIC API
    // =================

    public LogoServiceAdapter(LogoService logoService){
        this.logoService = logoService;
    }


    @Override
    public Optional<String> resolveUrl(String filename) {
        if(filename == null || filename.isBlank())return Optional.empty();
        return Optional.ofNullable(logoService.getLogoUrl(filename));
    }
}

package com.statseyes.studio.infrastructure.persistence.adapter;

import com.statseyes.studio.application.port.ImageResolvePort;
import com.statseyes.studio.domain.config.Resource_Type;
import com.statseyes.studio.domain.service.ImageService;

import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class ImageServiceAdapter implements ImageResolvePort {

    // =================
    // INSTANCE VARIABLE
    // =================

    private final ImageService imageService;

    // =================
    // PUBLIC API
    // =================

    public ImageServiceAdapter(ImageService imageService){
        this.imageService = imageService;
    }


    @Override
    public Optional<String> resolveUrl(String filename, Resource_Type dir) {
        if(filename == null || filename.isBlank())return Optional.empty();
        return Optional.ofNullable(imageService.getLogoUrl(filename, dir));
    }
}

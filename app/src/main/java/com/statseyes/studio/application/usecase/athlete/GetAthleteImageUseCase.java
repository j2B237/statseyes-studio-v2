package com.statseyes.studio.application.usecase.athlete;

import com.statseyes.studio.application.port.ImageResolvePort;
import com.statseyes.studio.domain.config.Resource_Type;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetAthleteImageUseCase {

    // ====================
    // INSTANCE VARIABLES
    // ====================

    // ====================
    // SPRING PORT
    // ====================

    private final ImageResolvePort imageResolve;


    // ================
    // PUBLIC API
    // ================

    public GetAthleteImageUseCase(ImageResolvePort imageResolve){
        this.imageResolve = imageResolve;
    }

    public Optional<String> execute(String imageUrl){
        return imageResolve.resolveUrl(imageUrl, Resource_Type.ATHLETE);
    }
}

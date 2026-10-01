package com.statseyes.studio.presentation.home;

import com.statseyes.studio.infrastructure.security.SessionAdapter;
import com.statseyes.studio.presentation.concurrent.BackgroundTaskRunner;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class HomeViewModel {

    // ========================
    // INSTANCE VARIABLES
    // ========================

    private final SessionAdapter sessionService;
    private final BackgroundTaskRunner backgroundTaskRunner;

    // =====================
    // PUBLIC API
    // =====================

    public HomeViewModel(
            SessionAdapter sessionService,
            BackgroundTaskRunner backgroundTaskRunner
    ){
        this.sessionService = sessionService;
        this.backgroundTaskRunner = backgroundTaskRunner;
    }

}

package com.statseyes.studio.application.usecase.athlete;

import com.statseyes.studio.domain.model.FilterFormOptions;
import com.statseyes.studio.application.port.FilterFormOptionsPort;

import org.springframework.stereotype.Component;

@Component
public class SearchAthleteUseCase {

    // ====================
    // INSTANCE VARIABLES
    // ===================

    private final FilterFormOptionsPort repository;

    public SearchAthleteUseCase(FilterFormOptionsPort repository){
        this.repository = repository;
    }

    public FilterFormOptions execute(Integer accountId, String token){
        return repository.search(accountId, token);
    }

}

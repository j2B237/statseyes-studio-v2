package com.statseyes.studio.application.usecase.athlete;

import com.statseyes.studio.application.port.AthleteRepositoryPort;

import com.statseyes.studio.domain.model.Athlete;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListAthletesUseCase {

    private final AthleteRepositoryPort repository;

    public ListAthletesUseCase(AthleteRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Athlete> execute(Integer accountId) {
        return repository.findAllByAccount(accountId);
    }
}

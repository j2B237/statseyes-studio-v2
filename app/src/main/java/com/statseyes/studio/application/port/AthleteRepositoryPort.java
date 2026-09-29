package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.Athlete;

import java.util.List;
import java.util.Optional;

public interface AthleteRepositoryPort {

    Athlete findById(Integer athleteId, Integer accountId);
    List<Athlete> findAllByAccount(Integer accountId);
}

package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.domain.model.NewAthlete;

import java.util.List;
import java.util.Optional;

public interface AthleteRepositoryPort {

    Athlete findById(Integer athleteId, Integer accountId);
    List<Athlete> findAllByAccount(Integer accountId);
    Athlete create(NewAthlete athlete, Integer AccountId);

}

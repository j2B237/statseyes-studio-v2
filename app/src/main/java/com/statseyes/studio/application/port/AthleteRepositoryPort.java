package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.domain.model.NewAthlete;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface AthleteRepositoryPort {

    @EntityGraph(attributePaths = {"position", "team", "secondaryPositions"})
    Athlete findById(Integer athleteId, Integer accountId);
    List<Athlete> findAllByAccount(Integer accountId);
    Athlete create(NewAthlete athlete, Integer AccountId);
    Athlete updateTransferAvailability(
            Integer athleteId,
            Integer accountId,
            boolean available
    );

    void updateImageURL(Integer athleteId, Integer accountId, String filename);

}

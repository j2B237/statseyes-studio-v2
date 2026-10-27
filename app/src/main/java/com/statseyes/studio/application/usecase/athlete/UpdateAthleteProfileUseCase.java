package com.statseyes.studio.application.usecase.athlete;

import com.statseyes.studio.application.port.AthleteProfilePort;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.domain.model.Availability;
import com.statseyes.studio.domain.model.DominantFoot;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;
@Component
public class UpdateAthleteProfileUseCase {

    /////////////////////////
    ///  CLASS VARIABLES ///
    ///////////////////////

    private final AthleteProfilePort port;

    // Constructor
    public UpdateAthleteProfileUseCase (AthleteProfilePort port){
        this.port = port;
    }

    public Athlete execute(
        Integer athleteId, Integer accountId, DominantFoot foot,
       Integer mainPositionId, Set<Integer> secondaryIds
    ){
        if (foot == null) throw new IllegalArgumentException("Le pied fort est obligatoire.");
        if (mainPositionId == null) throw new IllegalArgumentException("Le poste principal est obligatoire");

        Set<Integer> secondaries = new LinkedHashSet<>(secondaryIds == null ? Set.of() : secondaryIds);
        // Un poste ne peut etre principal et secondaire
        secondaries.remove(mainPositionId);

        return port.updateProfile(athleteId, accountId, foot, mainPositionId, secondaries);
    }
}

package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.domain.model.Availability;
import com.statseyes.studio.domain.model.DominantFoot;

import java.util.Set;

public interface AthleteProfilePort {
    Athlete updateProfile(Integer athleteId, Integer accountId, DominantFoot foot,
                          Integer mainPositionId, Set<Integer> secondaryPositionIds);
    Athlete updateAvailability(Integer athleteId, Integer accountId, Availability availability);
}

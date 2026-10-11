package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.AthleteFormOptions;

public interface AthleteFormOptionsPort {

    AthleteFormOptions load(Integer accountId);
}

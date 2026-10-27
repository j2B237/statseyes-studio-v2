package com.statseyes.studio.application.usecase.athlete;

import com.statseyes.studio.application.port.AthleteProfilePort;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.domain.model.Availability;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UpdateAthleteAvailabilityUseCase() {

     /////////////////////////
    /// CLASS VARIABLES  ////
    ////////////////////////

    private final AthleteProfilePort port;


    // Constructor
    public UpdateAthleteAvailabilityUseCase(AthleteProfilePort port){
        this.port = port;
    }


    public Athlete execute (Integer athleteId, Integer accountId, Availability input){
        Availability a = input == null || input.isAvailable() ? Availability.available() : input;

        if(!a.isAvailable()){
            LocalDate since = a.since() != null ? a.since() : LocalDate.now();
            if (a.expectedReturn() != null && a.expectedReturn().isBefore(since)){
                throw new IllegalArgumentException("La date de retour doit être apres la date de debut");
            }
            a = new Availability(a.status(), a.reason(), since, a.expectedReturn());
        }
        return port.updateAvailability(athleteId, accountId, a);
    }

}

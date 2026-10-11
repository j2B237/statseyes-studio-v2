package com.statseyes.studio.presentation.athletes.form;

import com.statseyes.studio.domain.model.LookupOption;

import java.time.LocalDate;

public record AthleteFormInput(
    String firstname,
    String lastname,
    LocalDate birthday,
    String gender,
    LookupOption team,
    LookupOption position,
    String height,
    String weight,
    String jersey,
    String nationalityCode
) {

}

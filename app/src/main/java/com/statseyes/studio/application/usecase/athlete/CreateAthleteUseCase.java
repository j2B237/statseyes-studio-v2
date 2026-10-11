package com.statseyes.studio.application.usecase.athlete;

import com.statseyes.studio.domain.exception.DomainValidationException;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.domain.model.NewAthlete;
import com.statseyes.studio.application.port.AthleteRepositoryPort;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class CreateAthleteUseCase {
    // ====================
    // INSTANCE VARIABLES
    // ===================

    private final AthleteRepositoryPort repository;

    public CreateAthleteUseCase(AthleteRepositoryPort repository){
        this.repository = repository;
    }


    public Athlete execute(NewAthlete in, Integer accountId){

        // Store empty string or remove every space if present
        String first = in.firstname() == null ? "" : in.firstname().trim();
        String last = in.lastname() == null ? "" : in.lastname().trim();

        if(first.isEmpty() || last.isEmpty())
            throw new DomainValidationException("Le prenom et le nom sont obligatoires.");

        if(first.length() > 150 || last.length() > 150)
            throw new DomainValidationException("Le prenom ou le nom trop long.");

        if(in.birthday() == null)
            throw new DomainValidationException("La date de naissance est obligatoire.");

        int age = Period.between(in.birthday(), LocalDate.now()).getYears();
        if(in.birthday().isAfter(LocalDate.now()) || age < 5 || age > 60)
            throw new DomainValidationException("Date de naissance invalide");

        if (in.gender() != 'M' && in.gender() != 'F')
            throw new DomainValidationException("Choisissez le sexe.");

        if (in.teamId() == null)
            throw new DomainValidationException("Choisissez une équipe.");

        if (in.positionId() == null)
            throw new DomainValidationException("Choisissez un poste.");

        if (in.heightCm() != null && (in.heightCm() < 100 || in.heightCm() > 230))
            throw new DomainValidationException("Taille entre 100 et 230 cm.");

        if (in.weightKg() != null && (in.weightKg() < 25 || in.weightKg() > 200))
            throw new DomainValidationException("Poids entre 25 et 200 kg.");

        if (in.jerseyNumber() != null && (in.jerseyNumber() < 0 || in.jerseyNumber() > 99))
            throw new DomainValidationException("Numéro de maillot entre 0 et 99.");

        return repository.create(
                new NewAthlete(
                        first, last, in.birthday(), in.gender(), in.teamId(),
                        in.positionId(), in.heightCm(), in.weightKg(),
                        in.jerseyNumber(), in.nationalityCode()
                ), accountId);
    }
}

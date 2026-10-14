package com.statseyes.studio.infrastructure.persistence.adapter;

import com.statseyes.studio.application.port.FilterFormOptionsPort;
import com.statseyes.studio.domain.model.Athlete;
import com.statseyes.studio.domain.model.AthleteLookupOption;
import com.statseyes.studio.domain.model.FilterFormOptions;
import com.statseyes.studio.domain.model.LookupFilterOption;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;

@Component
public class JpaAthleteFilterFormOptionsAdapter implements FilterFormOptionsPort{

    // ======================
    // INSTANCE VARIABLES
    // ======================

    private final EntityManager em;

    public JpaAthleteFilterFormOptionsAdapter(EntityManager em){
        this.em = em;
    }

    @Override
    @Transactional(readOnly = true)
    public FilterFormOptions load(Integer accountId){
        List<LookupFilterOption> teams = em.createQuery(
                "select new com.statseyes.studio.domain.model.LookupFilterOption(t.id, t.name)" +
                        "from TeamEntity t where t.account.id = :accountId order by t.name", LookupFilterOption.class)
                .setParameter("accountId", accountId).getResultList();

        List<LookupFilterOption> positions = em.createQuery(
                "select new com.statseyes.studio.domain.model.LookupFilterOption(p.id, p.name)" +
                        "from PositionEntity p order by p.name", LookupFilterOption.class)
                .getResultList();

        List<AthleteLookupOption> athletes = em.createQuery(
        "select new com.statseyes.studio.domain.model.AthleteLookupOption(" +
                "a.id, a.firstname, a.lastname, a.position.id, a.position.name, " +
                "a.team.id, a.team.name, a.imageUrl, a.available) " +
                "from AthleteEntity a where a.account.id = :accountId " +
                "order by a.firstname, a.lastname", AthleteLookupOption.class)
                .setParameter("accountId", accountId).getResultList();

        return new FilterFormOptions(teams, positions, athletes);
    }
}

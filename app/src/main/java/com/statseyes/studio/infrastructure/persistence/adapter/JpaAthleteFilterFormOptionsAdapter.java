package com.statseyes.studio.infrastructure.persistence.adapter;

import com.statseyes.studio.application.port.FilterFormOptionsPort;
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
    public FilterFormOptions search(Integer accountId, String token){
        List<LookupFilterOption> teams = em.createQuery(
                "select new com.statseyes.studio.domain.model.LookupFilterOption(t.id, t.name)" +
                        "from TeamEntity t where t.account.id = :a order by t.name", LookupFilterOption.class)
                .setParameter("a", accountId).getResultList();
        List<LookupFilterOption> positions = em.createQuery(
                "select new com.statseyes.studio.domain.model.LookupFilterOption(p.id, p.name)" +
                        "from PositionEntity p order by p.name", LookupFilterOption.class)
                .getResultList();
        List<LookupFilterOption> athletes = em.createQuery(
                "select new com.statseyes.studio.domain.model.LookupFilterOption(at.id, at.firstname )" +
                        "from AthleteEntity at where at.account.id = :a order by firstname", LookupFilterOption.class)
                .setParameter("a", accountId).getResultList();

        return new FilterFormOptions(teams, positions, athletes);
    }
}

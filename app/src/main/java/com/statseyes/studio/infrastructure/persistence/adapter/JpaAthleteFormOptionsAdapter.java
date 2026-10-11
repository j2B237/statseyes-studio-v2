package com.statseyes.studio.infrastructure.persistence.adapter;

import com.statseyes.studio.application.port.AthleteFormOptionsPort;
import com.statseyes.studio.domain.model.AthleteFormOptions;
import com.statseyes.studio.domain.model.LookupOption;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import jakarta.persistence.EntityManager;
import java.util.List;

@Component
public class JpaAthleteFormOptionsAdapter implements AthleteFormOptionsPort{

    // ======================
    // INSTANCE VARIABLES
    // ======================

    private final EntityManager em;


    // ====================
    // PUBLIC API
    // ====================

    public JpaAthleteFormOptionsAdapter(EntityManager em){
        this.em = em;
    }

    @Override
    @Transactional(readOnly = true)
    public AthleteFormOptions load(Integer accountId){
        List<LookupOption> teams = em.createQuery(
                "select new com.statseyes.studio.domain.model.LookupOption(t.id, t.name)" +
                        "from TeamEntity t where t.account.id = :a order by t.name", LookupOption.class
        ).setParameter("a", accountId).getResultList();

        // TODO : filtrer par sport quand plusieurs sports seront gérés
        List<LookupOption> positions = em.createQuery(
                "select new com.statseyes.studio.domain.model.LookupOption(p.id, p.name) " +
                        "from PositionEntity p order by p.name", LookupOption.class
        ).getResultList();

        return new AthleteFormOptions(teams, positions);
    }

}

package com.statseyes.studio.infrastructure.persistence.adapter;

import com.statseyes.studio.domain.model.GpsPoint;
import com.statseyes.studio.infrastructure.persistence.mapper.GpsPointMapper;
import com.statseyes.studio.application.port.SessionGpsPointsPort;
import com.statseyes.studio.infrastructure.persistence.repository.GpsPointJpaRepository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class JpaSessionGpsPointsAdapter implements SessionGpsPointsPort {

    // ==================
    // INSTANCE VARIABLES
    // ==================

    private final GpsPointJpaRepository repository;
    private final GpsPointMapper mapper;

    public JpaSessionGpsPointsAdapter(
            GpsPointJpaRepository repository,
            GpsPointMapper mapper
    ){
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    @Transactional(readOnly = true)
    public List<GpsPoint> findBySessionId(Integer importSessionId){
        return repository.findAllByImportedSession_Id(importSessionId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}

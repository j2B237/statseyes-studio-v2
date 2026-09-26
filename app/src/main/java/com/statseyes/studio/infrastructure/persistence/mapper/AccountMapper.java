package com.statseyes.studio.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.statseyes.studio.domain.model.AuthenticatedUser;
import com.statseyes.studio.infrastructure.persistence.entity.AccountEntity;

@Component
public class AccountMapper {

    public AuthenticatedUser toDomain(AccountEntity entity){
        return new AuthenticatedUser(
                entity.getId(),
                entity.getUsername(),
                entity.getFirst_name(),
                entity.getLast_name()
        );
    }
}

package com.statseyes.studio.domain.service;

import com.statseyes.studio.infrastructure.persistence.entity.AccountEntity;
import com.statseyes.studio.infrastructure.persistence.repository.AccountJpaRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountLookupService {

    private final AccountJpaRepository repository;

    public AccountLookupService(AccountJpaRepository repository){
        this.repository = repository;
    }

    @Cacheable(value = "accounts", key = "'username:' + #username")
    @Transactional(readOnly = true)
    public Optional<AccountEntity> findByUsername(String username){
        return repository.findByUsernameIgnoreCase(username);
    }
}

package com.statseyes.studio.infrastructure.security;

import com.statseyes.studio.application.port.AuthenticationPort;
import com.statseyes.studio.application.usecase.auth.AuthResult;
import com.statseyes.studio.domain.exception.AuthenticationFailedException;
import com.statseyes.studio.domain.model.AuthenticatedUser;
import com.statseyes.studio.infrastructure.persistence.entity.AccountEntity;
import com.statseyes.studio.infrastructure.persistence.mapper.AccountMapper;
import com.statseyes.studio.domain.service.AccountLookupService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AuthenticationAdapter implements AuthenticationPort {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String INVALID_CREDENTIALS_MESSAGE =
            "Aucun compte associe a ce nom d'utilisateur.\nVeuillez creer un compte.";

    private final AccountLookupService accountLookupService;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationAdapter(
        AccountLookupService accountLookupService,
        AccountMapper accountMapper,
        PasswordEncoder passwordEncoder
    ){
        this.accountLookupService = accountLookupService;
        this.accountMapper = accountMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public AuthResult authenticate(String username, String rawPassword){
        AccountEntity account = accountLookupService.findByUsername(username)
                .orElseThrow(() ->
                        new AuthenticationFailedException(INVALID_CREDENTIALS_MESSAGE)
                );

        if (!passwordEncoder.matches(rawPassword, account.getPasswordHash())){
            throw new AuthenticationFailedException(INVALID_CREDENTIALS_MESSAGE);
        }

        AuthenticatedUser user = accountMapper.toDomain(account);
        return new AuthResult(user, generateOpaqueToken());
    }

    private String generateOpaqueToken(){
        byte[] tokenBytes = new byte[2];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}

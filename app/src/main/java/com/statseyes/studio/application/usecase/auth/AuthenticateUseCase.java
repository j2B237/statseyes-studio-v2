package com.statseyes.studio.application.usecase.auth;

import com.statseyes.studio.application.port.AuthenticationPort;
import com.statseyes.studio.application.port.UserSessionPort;
import com.statseyes.studio.domain.config.Error_Type;

import org.springframework.stereotype.Component;

@Component
public class AuthenticateUseCase {

    private final AuthenticationPort authenticationPort;
    private final UserSessionPort userSessionPort;

    //
    // Constructor
    //

    public AuthenticateUseCase(
        AuthenticationPort authenticationPort,
        UserSessionPort userSessionPort
    ){
        this.authenticationPort = authenticationPort;
        this.userSessionPort = userSessionPort;
    }

    //
    // Public API
    //
    public Error_Type execute(String username, String rawPassword){
        if(username == null || username.isBlank()){
            return Error_Type.EMPTY_USERNAME;
        }
        if(rawPassword == null || rawPassword.isBlank()){
           return Error_Type.EMPTY_PASSWORD;
        }

        AuthResult result = authenticationPort.authenticate(username, rawPassword);
        return userSessionPort.open(result.user(), result.token());
    }
}

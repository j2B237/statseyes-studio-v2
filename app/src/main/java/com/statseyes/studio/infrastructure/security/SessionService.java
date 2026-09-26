package com.statseyes.studio.infrastructure.security;

import com.statseyes.studio.application.port.UserSessionPort;
import com.statseyes.studio.domain.config.Error_Type;
import com.statseyes.studio.domain.model.AuthenticatedUser;

import lombok.Getter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;


import org.springframework.stereotype.Component;

/**
* <p>SessionService is a spring component used to manage
* user session through navigation in the app.
* Spring already manages this as a singleton bean (default scope),
* so no manual singleton pattern is needed here.
 * </p>
*
 */

@Component
public class SessionService implements UserSessionPort {

    private final ObjectProperty<AuthenticatedUser> currentUser =
            new SimpleObjectProperty<>();

    @Getter
    private String jwtToken;

    public ObjectProperty<AuthenticatedUser> currentUserProperty(){
        return currentUser;
    }

    public AuthenticatedUser getCurrentUser(){
        return currentUser.get();
    }

    // Implementation du port - utilise par AuthenticateUseCase

    @Override
    public Error_Type open(AuthenticatedUser user, String token){
        currentUser.set(user);
        jwtToken = token;
        return ((currentUser == null) || (jwtToken == null || jwtToken.isBlank()))
                ? Error_Type.SESSION_LOGGING_FAILED :
                Error_Type.SESSION_LOGGING_SUCCESS;

    }

    @Override
    public Error_Type isAuthenticated(){
        return ((currentUser.get() != null) && jwtToken != null)
                ? Error_Type.AUTHENTICATION_SUCCESS : Error_Type.AUTHENTICATION_FAILED;
    }

    @Override
    public void clear(){
        if(currentUser.get() != null || jwtToken != null){
            currentUser.set(null);
            jwtToken = null;
        }
    }
}

package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.config.Error_Type;
import com.statseyes.studio.domain.model.AuthenticatedUser;

public interface UserSessionPort {
    Error_Type open(AuthenticatedUser user, String token);
    void clear();
    Error_Type isAuthenticated();
}

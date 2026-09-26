package com.statseyes.studio.application.port;
import com.statseyes.studio.application.usecase.auth.AuthResult;
import org.springframework.stereotype.Component;

/**
 * @ throw com.statseyes.studio.domain.exception.AuthenticationFailedException
 * if invalid
 */

@Component
public interface AuthenticationPort {
    AuthResult authenticate(String username, String rawPassword);
}

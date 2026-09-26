package com.statseyes.studio.application.usecase.auth;

import com.statseyes.studio.domain.model.AuthenticatedUser;

/**
 * AuthResult is an immutable object that serves as contract between
 * an account and an Authenticated User.
 * @param user POJO
 * @param token Random Base64 token
 */
public record AuthResult(AuthenticatedUser user, String token) {
}

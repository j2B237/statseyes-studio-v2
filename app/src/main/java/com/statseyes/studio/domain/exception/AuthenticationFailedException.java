package com.statseyes.studio.domain.exception;

/**
 * AuthenticationFailedException : Exception raise when
 * authentication process failed.
 */
public class AuthenticationFailedException extends RuntimeException{
    public AuthenticationFailedException(String message){
        super(message);
    }
}

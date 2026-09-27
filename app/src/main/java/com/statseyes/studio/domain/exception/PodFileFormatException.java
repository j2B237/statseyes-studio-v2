package com.statseyes.studio.domain.exception;

public class PodFileFormatException extends RuntimeException {
    public PodFileFormatException(String message) {
        super(message);
    }
    public PodFileFormatException(String message, Throwable cause){
        super(message, cause);
    }
}

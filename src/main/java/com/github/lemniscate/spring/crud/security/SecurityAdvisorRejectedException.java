package com.github.lemniscate.spring.crud.security;

/**
 * Created by dave on 2/11/15.
 */
public class SecurityAdvisorRejectedException extends RuntimeException{
    public SecurityAdvisorRejectedException() {
    }

    public SecurityAdvisorRejectedException(String message) {
        super(message);
    }

    public SecurityAdvisorRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecurityAdvisorRejectedException(Throwable cause) {
        super(cause);
    }

    public SecurityAdvisorRejectedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}

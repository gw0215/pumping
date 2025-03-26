package com.pumping.domain.emailverification.exception;

public class CodeVerificationException extends RuntimeException {
    public CodeVerificationException(String message) {
        super(message);
    }
}
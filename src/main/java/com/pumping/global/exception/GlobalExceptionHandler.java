package com.pumping.global.exception;

import com.pumping.domain.emailverification.exception.CodeVerificationException;
import com.pumping.domain.member.exception.EmailAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.UncheckedIOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UncheckedIOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleFileProcessingException(UncheckedIOException e) {
        log.error("{}",e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(CodeVerificationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEmailVerificationException(CodeVerificationException e) {
        log.error("{}",e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlePasswordEncodingException(RuntimeException e) {
        log.error("{}",e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("{}",e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEmail(EmailAlreadyExistsException e) {
        log.error("{}",e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}

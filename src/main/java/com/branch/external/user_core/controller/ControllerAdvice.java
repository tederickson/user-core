package com.branch.external.user_core.controller;

import com.branch.external.user_core.exception.InvalidRequestException;
import com.branch.external.user_core.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(final InvalidRequestException exception) {
        log.error("handleBadRequest {}", exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(final NotFoundException exception) {
        log.info("handleNotFound {}", exception.getMessage());
        return exception.getMessage();
    }
}

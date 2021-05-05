package com.smarchoice.product.adapter.service.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class IncompleteExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IncompleteException.class)
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, "At lease one provider is unreadable", new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }
}


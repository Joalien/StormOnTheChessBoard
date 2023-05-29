package fr.kubys.controller;

import fr.kubys.repository.GameNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    protected ResponseEntity<Object> handleConflict(GameNotFoundException e, WebRequest request) {
        return handleExceptionInternal(e, null, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
package fr.kubys.controller;

import fr.kubys.board.IllegalMoveException;
import fr.kubys.card.CardNotFoundException;
import fr.kubys.card.params.CardParamException;
import fr.kubys.mapper.MappingException;
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
    protected ResponseEntity<Object> handleNotFoundException(GameNotFoundException e, WebRequest request) {
        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({CardNotFoundException.class, IllegalStateException.class, MappingException.class, IllegalArgumentException.class, IllegalMoveException.class, CardParamException.class})
    protected ResponseEntity<Object> handleBadRequestException(Exception e, WebRequest request) {
        return createResponseEntity(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
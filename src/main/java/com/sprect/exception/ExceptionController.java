package com.sprect.exception;

import com.sprect.model.response.ResponseError;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.ValidationFailureException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler({UsernameNotFoundException.class, NotFoundException.class})
    protected ResponseEntity<Object> NntFound(Exception ex) {
        return new ResponseEntity<>(new ResponseError(
                new Date().toString(),
                404, ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({JwtException.class, ExpiredJwtException.class})
    protected ResponseEntity<Object> jwt(Exception ex) {
        return new ResponseEntity<>(new ResponseError(
                new Date().toString(),
                401, ex.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ValidationFailureException.class, RegistrationException.class})
    protected ResponseEntity<Object> badRequest(Exception ex) {
        return new ResponseEntity<>(new ResponseError(
                new Date().toString(),
                400, ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({TryAuthException.class, RightEditException.class, StatusException.class})
    protected ResponseEntity<Object> accessException(Exception ex) {
        return new ResponseEntity<>(new ResponseError(
                new Date().toString(),
                403, ex.getMessage()),
                HttpStatus.FORBIDDEN);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDate.now());
        body.put("status", status.value());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        body.put("error", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}

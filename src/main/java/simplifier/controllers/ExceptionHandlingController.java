package simplifier.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> shortenedNotUnique(MethodArgumentNotValidException ex) {
        String responseMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> invalidCredentials(AuthenticationException ae) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("username or password is incorrect");
    }
}


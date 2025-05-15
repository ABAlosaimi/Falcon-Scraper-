package com.proxy.falcon.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(UnvalidURLException.class)
    public ResponseEntity<Object> handleRuntimeException(UnvalidURLException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }


    @ExceptionHandler(ParsePageException.class)
    public ResponseEntity<Object> handleParsePageException(ParsePageException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    
}

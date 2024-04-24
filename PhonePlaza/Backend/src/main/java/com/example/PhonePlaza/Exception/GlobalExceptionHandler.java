package com.example.PhonePlaza.Exception;

import com.example.PhonePlaza.Common.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity handleException(Exception e){

        APIResponse apiResponse=new APIResponse();

        apiResponse.setError("Oops Something went wrong");
        apiResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(apiResponse);
    }
    @ExceptionHandler
    public ResponseEntity handleAccessDeniedException(AccessDeniedException e) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @ExceptionHandler
    public ResponseEntity handleProductNotFoundException(ProductNotFoundException e) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setError(e.getMessage());
        apiResponse.setStatus(HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(apiResponse);
    }
}

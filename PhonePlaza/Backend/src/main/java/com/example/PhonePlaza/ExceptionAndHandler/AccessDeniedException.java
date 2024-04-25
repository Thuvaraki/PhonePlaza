package com.example.PhonePlaza.ExceptionAndHandler;

public class AccessDeniedException extends RuntimeException{
    public AccessDeniedException(String message){
        super(message);
    }
}


package com.example.PhonePlaza.ExceptionAndHandler;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
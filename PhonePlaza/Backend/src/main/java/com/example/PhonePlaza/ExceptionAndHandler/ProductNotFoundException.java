package com.example.PhonePlaza.ExceptionAndHandler;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}

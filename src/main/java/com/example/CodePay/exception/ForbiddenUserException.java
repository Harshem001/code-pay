package com.example.CodePay.exception;

public class ForbiddenUserException extends RuntimeException {
    public ForbiddenUserException(String message) {
        super(message);
    }
}

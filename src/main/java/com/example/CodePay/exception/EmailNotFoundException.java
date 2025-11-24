package com.example.CodePay.exception;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException() {
        super("No User is with the Email");
    }
}

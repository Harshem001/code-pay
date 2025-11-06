package com.example.CodePay.exception;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException() {
        super("Email is not correct");
    }
}

package com.example.CodePay.Authentication;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException() {
        super("Email is not correct");
    }
}

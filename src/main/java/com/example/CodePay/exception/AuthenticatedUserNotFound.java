package com.example.CodePay.exception;

public class AuthenticatedUserNotFound extends RuntimeException {
    public AuthenticatedUserNotFound() {
        super("You are not logged in");
    }
}

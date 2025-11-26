package com.example.CodePay.exception;

public class InsufficientFundException extends RuntimeException {
    public InsufficientFundException() {

        super("Insufficient Fund");
    }
}

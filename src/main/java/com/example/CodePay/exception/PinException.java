package com.example.CodePay.exception;

public class PinException extends RuntimeException {
    public PinException() {

        super("Pin must be 4 or 6 digits");
    }
    public PinException(String message) {
        super(message);
    }
}

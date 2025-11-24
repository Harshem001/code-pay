package com.example.CodePay.exception;

public class PhoneNumberAlreadyExistException extends RuntimeException {
    public PhoneNumberAlreadyExistException() {
        super("Phone number already exist");
    }
}

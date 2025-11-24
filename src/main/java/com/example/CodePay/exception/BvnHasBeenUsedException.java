package com.example.CodePay.exception;

public class BvnHasBeenUsedException extends RuntimeException {
    public BvnHasBeenUsedException() {

        super("BVN has been used");
    }
}

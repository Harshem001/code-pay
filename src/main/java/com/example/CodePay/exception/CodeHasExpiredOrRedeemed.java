package com.example.CodePay.exception;

public class CodeHasExpiredOrRedeemed extends RuntimeException {
    public CodeHasExpiredOrRedeemed(String message) {
        super(message);
    }
}

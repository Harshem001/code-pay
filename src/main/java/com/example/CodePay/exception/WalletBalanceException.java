package com.example.CodePay.exception;

public class WalletBalanceException extends RuntimeException {
    public WalletBalanceException()
    {
        super("Insufficient balance");
    }
}

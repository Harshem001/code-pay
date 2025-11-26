package com.example.CodePay.exception;

public class WalletNumberException extends RuntimeException {
    public WalletNumberException()
    {
        super("Wallet Number not found");
    }
}

package com.example.CodePay.exception;

public class WalletException extends RuntimeException {
    public WalletException() {

        super("Sender doesn't have a wallet account");
    }
    public WalletException(String message) {
        super(message);
    }
}

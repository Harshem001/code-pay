package com.example.CodePay.wallet;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/wallet-wallet")
public class Wallet_to_WalletController {

    private final Wallet_to_WalletService wallet_to_WalletService;

    @PostMapping
    public ResponseEntity<Wallet_to_Wallet_Sender_Response> walletTransfer (
            Authentication authentication,
            @RequestBody Wallet_to_Wallet_Sender_Request request){

        return  ResponseEntity.ok(wallet_to_WalletService.walletTransfer(authentication, request));

    }
}
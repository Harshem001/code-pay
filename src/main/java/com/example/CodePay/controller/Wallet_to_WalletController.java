package com.example.CodePay.controller;

import com.example.CodePay.Security.UserPrincipal;
import com.example.CodePay.dto.TransactionHistoryDTO;
import com.example.CodePay.dto.Wallet_to_Wallet_Sender_Request;
import com.example.CodePay.dto.Wallet_to_Wallet_Sender_Response;
import com.example.CodePay.repo.PaymentRepository;
import com.example.CodePay.entity.User;
import com.example.CodePay.repo.UserRepository;
import com.example.CodePay.service.Wallet_to_WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/wallet-wallet")
@Tag(name = "Wallet to Wallet Transfer", description = "Endpoint for making transfer between user that has account with CodePay")
public class Wallet_to_WalletController {

    private final Wallet_to_WalletService wallet_to_WalletService;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @PostMapping
    @Operation(
            summary = "Wallet to Wallet Transfer",
            description = "Transfer funds from users that account with CodePay",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transfer Successful"),
                    @ApiResponse(responseCode = "400", description = "Error transferring funds")
            }
    )
    public ResponseEntity<Wallet_to_Wallet_Sender_Response> walletTransfer (
            Authentication authentication,
            @RequestBody Wallet_to_Wallet_Sender_Request request){

        return  ResponseEntity.ok(wallet_to_WalletService.walletTransfer(authentication, request));

    }
    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> showBalance(@AuthenticationPrincipal UserPrincipal userPrincipal){
        User user = userRepository.findByEmail(userPrincipal.getEmail()).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));
        BigDecimal balance = user.getWallet().getBalance();
        return ResponseEntity.ok(balance);
    }
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionHistoryDTO>> showTransactionHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal){
        List<TransactionHistoryDTO> transactionHistory = wallet_to_WalletService.getTransactionHistory(userPrincipal);
        return ResponseEntity.ok(transactionHistory);
    }

}
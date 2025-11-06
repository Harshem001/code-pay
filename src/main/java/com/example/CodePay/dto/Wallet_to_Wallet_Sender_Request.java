package com.example.CodePay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Wallet_to_Wallet_Sender_Request {
    private BigDecimal amount;

    @NotBlank(message = "wallet number must be 10 digits")
    private String receiverWalletNumber;

    private String pin;
}

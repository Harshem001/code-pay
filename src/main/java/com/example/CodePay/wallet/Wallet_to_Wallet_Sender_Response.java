package com.example.CodePay.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Wallet_to_Wallet_Sender_Response {
    private BigDecimal amount;
    private String message; // successful or failed
    private String receiverName;
    private String reference;
}

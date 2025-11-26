package com.example.CodePay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Wallet_to_Wallet_Sender_Response {
    private BigDecimal amount;
    private String message; // successful or failed
    private String receiverName;
    private String reference;
    private LocalDateTime dateTime;
}

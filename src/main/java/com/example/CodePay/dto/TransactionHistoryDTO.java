package com.example.CodePay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
@AllArgsConstructor
@Data
public class TransactionHistoryDTO {
    private String sender;
    private String reference;
    private BigDecimal amount;
    private String status;
    private String transactionType;
    private Instant date;
}

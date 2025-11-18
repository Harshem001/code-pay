package com.example.CodePay.dto;

import com.example.CodePay.enums.Status;
import com.example.CodePay.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
@AllArgsConstructor
@Data
public class TransactionHistoryDTO {
    private String sender;
    private String phoneNumber;
    private String reference;
    private BigDecimal amount;
    private Status status;
    private TransactionType transactionType;
    private Instant date;
}

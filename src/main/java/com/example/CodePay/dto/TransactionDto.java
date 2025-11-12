package com.example.CodePay.dto;

import com.example.CodePay.entity.User;
import com.example.CodePay.enums.Status;
import com.example.CodePay.enums.TransactionEntry;
import com.example.CodePay.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDto {
    private Long transactionId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private Status  status;
    private TransactionEntry entry;
    private Instant date;
}

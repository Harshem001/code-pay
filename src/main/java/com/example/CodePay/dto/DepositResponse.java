package com.example.CodePay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DepositResponse {
    private String authorizationUrl;
    private Long transactionId;
    private Boolean status;
    private String reference;
    private BigDecimal amount;
    private Instant date;
}
